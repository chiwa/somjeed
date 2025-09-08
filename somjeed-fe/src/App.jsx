import { useEffect, useRef, useState } from "react";
import "./App.css";

const API_CHAT = "/api/chat";
const API_FEEDBACK = "/api/feedback";

const DELAY_MS = 1200;
const sleep = (ms) => new Promise(res => setTimeout(res, ms));

export default function App() {
  const [items, setItems] = useState([]);                   // [{role:'bot'|'user', text, italic?}]
  const [text, setText] = useState("");
  const [disabled, setDisabled] = useState(false);          // ปิดทั้งหมดหลัง feedback
  const [awaitingFeedback, setAwaitingFeedback] = useState(false); // รอคะแนนอยู่ไหม
  const [sending, setSending] = useState(false);            // สถานะกำลังส่ง request
  const sessionId = useRef(crypto.randomUUID());
  const nudgeTimer = useRef(null);
  const goodbyeTimer = useRef(null);
  const booted = useRef(false);
  const inputRef = useRef(null);

  // ฟังก์ชันโฟกัสช่องพิมพ์หลัง render เสมอ
  const focusInput = () => {
    requestAnimationFrame(() => inputRef.current?.focus());
  };

  const add = (role, text, italic = false) => {
    setItems(prev => {
      const next = [...prev, { role, text, italic }];
      // Auto-scroll ให้เห็นข้อความล่าสุดเสมอ
      requestAnimationFrame(() => {
        const panel = document.getElementById("chat");
        if (panel) panel.scrollTop = panel.scrollHeight;
      });
      return next;
    });
  };

  async function callChat(message, firstMessage = false) {
    const res = await fetch(API_CHAT, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        sessionId: sessionId.current,
        firstMessage,
        message
      })
    });
    const data = await res.json();
    const msgs = data.messages || [];

    // แสดงทีละข้อความพร้อมดีเลย์
    for (let i = 0; i < msgs.length; i++) {
      const m = msgs[i];

      // บรรทัดแรกของ firstMessage ให้เป็นตัวเอียง (greeting)
      add("bot", m, firstMessage && i === 0);

      // ถ้าเป็นข้อความ goodbye ขอเรตติ้ง → เปิดโหมด feedback + ล็อกอินพุต
      if (m.includes("Before you go, could you rate")) {
        setAwaitingFeedback(true);
        document.body.classList.add("show-emoji");
      }

      // เว้นจังหวะระหว่างข้อความ (ตัวสุดท้ายไม่ต้องรอ)
      if (i < msgs.length - 1) {
        await sleep(DELAY_MS);
      }
    }
  }

  function resetTimers() {
    if (nudgeTimer.current) clearTimeout(nudgeTimer.current);
    if (goodbyeTimer.current) clearTimeout(goodbyeTimer.current);
    document.body.classList.remove("show-emoji");
  }

  function startSilenceFlow() {
    if (disabled) return;
    resetTimers();
    // 10s เงียบ → #nudge
    nudgeTimer.current = setTimeout(async () => {
      await callChat("#nudge");
      // อีก 10s เงียบ → #goodbye + โชว์ emoji
      goodbyeTimer.current = setTimeout(async () => {
        await callChat("#goodbye");
        if (!disabled) document.body.classList.add("show-emoji");
      }, 10000);
    }, 10000);
  }

  async function send() {
    if (disabled || awaitingFeedback || sending) return;
    const msg = text.trim();
    if (!msg) {
      focusInput();
      return;
    }
    add("user", msg);
    setText("");
    setSending(true);
    resetTimers();
    try {
      await callChat(msg);
    } catch (e) {
      add("bot", "Sorry, the server is unavailable right now.");
    } finally {
      setSending(false);
      startSilenceFlow();
      focusInput(); // โฟกัสกลับช่องพิมพ์เสมอหลังส่งเสร็จ
    }
  }

  async function sendFeedback(score) {
    if (disabled) return;
    const face = score === 5 ? "😊" : score === 3 ? "😐" : "😞";
    add("user", face);
    document.body.classList.remove("show-emoji");

    // ยิงไปที่ /api/feedback แล้วแสดง message จาก BE
    try {
      const res = await fetch(API_FEEDBACK, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ sessionId: sessionId.current, rating: score })
      });
      const data = await res.json(); // { message: "Thanks! ..." }
      if (data?.message) add("bot", data.message);
      else add("bot", "Thanks! Your feedback has been recorded.");
    } catch (e) {
      add("bot", "Sorry, could not record feedback right now.");
    }

    // ปิดการสนทนา: disable input + ปุ่ม send และหยุดนาฬิกา
    setDisabled(true);
    setAwaitingFeedback(false);
    resetTimers();
  }

  // บูตครั้งแรก
  useEffect(() => {
    if (booted.current) return;
    booted.current = true;
    (async () => {
      await callChat("", true); // Greeting + Prediction
      startSilenceFlow();
      focusInput(); // โฟกัสช่องพิมพ์เมื่อหน้าโหลดเสร็จ
    })();
    return resetTimers;
  }, []);

  // รีโฟกัสเมื่อ “พร้อมให้พิมพ์” (ไม่ disabled, ไม่ waiting feedback, ไม่กำลังส่ง)
  useEffect(() => {
    if (!disabled && !awaitingFeedback && !sending) {
      focusInput();
    }
  }, [disabled, awaitingFeedback, sending, items.length]);

  return (
    <div className="wrap">
      <h2>Somjeed Chat Demo</h2>

      <div className="panel" id="chat">
        {items.map((m, i) => (
          <div key={i} className={`line ${m.role}`}>
            <b className="name">{m.role === "user" ? "User" : "Somjeed"}:</b>{" "}
            <span className={m.italic ? "italic" : ""}>{m.text}</span>
          </div>
        ))}
      </div>

      {/* แสดงปุ่มอีโมจิเมื่ออยู่ในโหมดรอ feedback และยังไม่ถูกปิด */}
      {awaitingFeedback && !disabled && (
        <div className="emoji-row">
          <button onClick={() => sendFeedback(5)} title="good" disabled={disabled}>😊</button>
          <button onClick={() => sendFeedback(3)} title="okay" disabled={disabled}>😐</button>
          <button onClick={() => sendFeedback(1)} title="bad" disabled={disabled}>😞</button>
        </div>
      )}

      <div className="bar">
        {/* โฟกัสอินพุตด้วย inputRef */}
        <input
          ref={inputRef}
          autoFocus
          value={text}
          onChange={e => setText(e.target.value)}
          onKeyDown={e => e.key === "Enter" && !(disabled || awaitingFeedback || sending) && send()}
          placeholder='Type… (try: Yes, "statement", "lost card", "points", "redeem", "balance")'
          disabled={disabled || awaitingFeedback || sending}
        />
        {/* กันปุ่มแย่งโฟกัสด้วย onMouseDown.preventDefault() */}
        <button
          onMouseDown={(e) => e.preventDefault()}
          onClick={send}
          disabled={disabled || awaitingFeedback || sending}
        >
          {sending ? "Sending..." : "Send"}
        </button>
      </div>
    </div>
  );
}