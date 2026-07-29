import express from "express";
import path from "path";
import { createServer as createViteServer } from "vite";
import { GoogleGenAI } from "@google/genai";

const app = express();
const PORT = 3000;

app.use(express.json());

// Server-side Gemini API route for AI Tutor Chatbot
app.post("/api/chat", async (req, res) => {
  try {
    const { message, history } = req.body;
    if (!message) {
      return res.status(400).json({ error: "Message prompt is required" });
    }

    const apiKey = process.env.GEMINI_API_KEY;
    if (!apiKey) {
      return res.status(500).json({
        error: "GEMINI_API_KEY environment variable is not configured on the server."
      });
    }

    const ai = new GoogleGenAI({
      apiKey,
      httpOptions: {
        headers: {
          "User-Agent": "aistudio-build",
        },
      },
    });

    const contents: Array<{ role: "user" | "model"; parts: Array<{ text: string }> }> = [];

    if (history && Array.isArray(history)) {
      history.forEach((h: { role: string; parts: string }) => {
        contents.push({
          role: h.role === "user" ? "user" : "model",
          parts: [{ text: h.parts }],
        });
      });
    }
    contents.push({ role: "user", parts: [{ text: message }] });

    const response = await ai.models.generateContent({
      model: "gemini-3.6-flash",
      contents,
      config: {
        systemInstruction:
          "You are 'Towfik Edutips AI Tutor', an expert WBBSE Madhyamik (Class 10) exam preparation assistant created for Towfik Edutips students. You specialize in Bengali, English, History, Geography, Physical Science, Life Science, and Mathematics for WBBSE board students. If asked who created you or what model you are, always state that you are Towfik Edutips AI Tutor developed for Madhyamik students. Provide clear, accurate, encouraging, and detailed answers with marks distribution guidance when requested. Format with bold headings, bullet points, and clear Bengali or English explanations.",
      },
    });

    const reply = response.text || "Sorry, I could not generate a response. Please try again.";
    return res.json({ reply });
  } catch (error: any) {
    console.error("Gemini API Error in /api/chat:", error);
    return res.status(500).json({
      error: error.message || "Failed to communicate with AI Tutor"
    });
  }
});

async function startServer() {
  if (process.env.NODE_ENV !== "production") {
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: "spa",
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.join(process.cwd(), "dist");
    app.use(express.static(distPath));
    app.get("*", (req, res) => {
      res.sendFile(path.join(distPath, "index.html"));
    });
  }

  app.listen(PORT, "0.0.0.0", () => {
    console.log(`Server running on http://0.0.0.0:${PORT}`);
  });
}

startServer();
