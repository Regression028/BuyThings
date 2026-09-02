const express = require("express");
const cors = require("cors");
const crypto = require("crypto");
const Razorpay = require("razorpay");
require("dotenv").config();

const app = express();
const PORT = process.env.PORT || 5001;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Initialize Razorpay instance
const razorpay = new Razorpay({
    key_id: process.env.RAZORPAY_KEY_ID,
    key_secret: process.env.RAZORPAY_KEY_SECRET,
});

// -------------------------------
// 1. Health Check Endpoint
// -------------------------------
app.get("/", (req, res) => {
    res.json({
        status: "ok",
        message: "BuyThings Razorpay Backend API is running!",
        timestamp: new Date().toISOString()
    });
});

// -------------------------------
// 2. Get Public Razorpay Key ID
// -------------------------------
app.get("/api/payment/key", (req, res) => {
    if (!process.env.RAZORPAY_KEY_ID) {
        return res.status(500).json({
            success: false,
            message: "Razorpay Key ID is not configured on the server."
        });
    }
    res.json({
        success: true,
        keyId: process.env.RAZORPAY_KEY_ID
    });
});

// -------------------------------
// 3. Create Razorpay Order
// -------------------------------
app.post("/api/payment/create-order", async (req, res) => {
    try {
        const { amount, amountInPaise, currency = "INR", receipt, notes } = req.body;

        if (!amount && !amountInPaise) {
            return res.status(400).json({
                success: false,
                message: "Amount is required (in Rupees or in Paise)."
            });
        }

        // Razorpay expects amount in smallest currency unit (e.g. Paise for INR: 100 INR = 10000 Paise)
        const finalAmountInPaise = amountInPaise ? Math.round(amountInPaise) : Math.round(Number(amount) * 100);

        if (isNaN(finalAmountInPaise) || finalAmountInPaise <= 0) {
            return res.status(400).json({
                success: false,
                message: "Invalid amount provided."
            });
        }

        const options = {
            amount: finalAmountInPaise,
            currency: currency.toUpperCase(),
            receipt: receipt || `receipt_${Date.now()}`,
            notes: notes || {}
        };

        const order = await razorpay.orders.create(options);

        console.log(`[ORDER CREATED] Order ID: ${order.id}, Amount: ${order.amount} ${order.currency}`);

        res.status(200).json({
            success: true,
            message: "Order created successfully",
            order: {
                id: order.id,
                entity: order.entity,
                amount: order.amount, // in paise
                amount_due: order.amount_due,
                amount_paid: order.amount_paid,
                currency: order.currency,
                receipt: order.receipt,
                status: order.status,
                created_at: order.created_at
            },
            keyId: process.env.RAZORPAY_KEY_ID
        });
    } catch (error) {
        console.error("[CREATE ORDER ERROR]", error);
        res.status(500).json({
            success: false,
            message: error.description || error.message || "Failed to create Razorpay order",
            error
        });
    }
});

// -------------------------------
// 4. Verify Razorpay Payment Signature
// -------------------------------
app.post("/api/payment/verify-payment", (req, res) => {
    try {
        const { razorpay_order_id, razorpay_payment_id, razorpay_signature } = req.body;

        if (!razorpay_order_id || !razorpay_payment_id || !razorpay_signature) {
            return res.status(400).json({
                success: false,
                message: "Missing required fields: razorpay_order_id, razorpay_payment_id, razorpay_signature"
            });
        }

        // Signature generated as: HMAC_SHA256(order_id + "|" + payment_id, secret)
        const body = razorpay_order_id + "|" + razorpay_payment_id;
        const expectedSignature = crypto
            .createHmac("sha256", process.env.RAZORPAY_KEY_SECRET)
            .update(body)
            .digest("hex");

        const isSignatureValid = crypto.timingSafeEqual(
            Buffer.from(expectedSignature, "utf-8"),
            Buffer.from(razorpay_signature, "utf-8")
        );

        if (isSignatureValid) {
            console.log(`[PAYMENT SUCCESS] Order: ${razorpay_order_id}, Payment: ${razorpay_payment_id}`);

            // TODO: Update order status in database (e.g. MongoDB/Firestore/SQL)

            return res.status(200).json({
                success: true,
                message: "Payment verified successfully",
                orderId: razorpay_order_id,
                paymentId: razorpay_payment_id
            });
        } else {
            console.warn(`[PAYMENT VERIFICATION FAILED] Signature mismatch for Order: ${razorpay_order_id}`);
            return res.status(400).json({
                success: false,
                message: "Invalid payment signature. Verification failed."
            });
        }
    } catch (error) {
        console.error("[VERIFY PAYMENT ERROR]", error);
        res.status(500).json({
            success: false,
            message: "Internal server error during payment verification",
            error: error.message
        });
    }
});

// -------------------------------
// 5. Razorpay Webhook Endpoint
// -------------------------------
app.post("/api/payment/webhook", (req, res) => {
    try {
        const webhookSecret = process.env.RAZORPAY_WEBHOOK_SECRET;
        const razorpaySignature = req.headers["x-razorpay-signature"];

        if (webhookSecret && razorpaySignature) {
            const shasum = crypto.createHmac("sha256", webhookSecret);
            shasum.update(JSON.stringify(req.body));
            const digest = shasum.digest("hex");

            if (digest !== razorpaySignature) {
                console.warn("[WEBHOOK INVALID] Invalid signature");
                return res.status(400).json({ success: false, message: "Invalid webhook signature" });
            }
        }

        const event = req.body.event;
        const payload = req.body.payload;

        console.log(`[WEBHOOK EVENT] ${event}`);

        switch (event) {
            case "payment.captured": {
                const payment = payload.payment.entity;
                console.log(`Payment Captured: ${payment.id} for Order: ${payment.order_id}`);
                // Handle payment capture logic (e.g., mark order as paid in DB)
                break;
            }
            case "payment.failed": {
                const payment = payload.payment.entity;
                console.log(`Payment Failed: ${payment.id} for Order: ${payment.order_id}`);
                // Handle payment failure logic
                break;
            }
            case "order.paid": {
                const order = payload.order.entity;
                console.log(`Order Paid: ${order.id}`);
                break;
            }
            default:
                console.log(`Unhandled event type: ${event}`);
        }

        res.status(200).json({ status: "ok" });
    } catch (error) {
        console.error("[WEBHOOK ERROR]", error);
        res.status(500).json({ success: false, message: "Webhook error", error: error.message });
    }
});

// Start Server
app.listen(PORT, () => {
    console.log(`=================================`);
    console.log(`🚀 BuyThings Backend Running!`);
    console.log(`📡 Port: ${PORT}`);
    console.log(`💳 Razorpay Integration Ready`);
    console.log(`=================================`);
});
