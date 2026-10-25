import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./RecommendationChat.css";
import { API_BASE } from '../config';

const preferenceSuggestions = [
  "Gentle stories",
  "Funny",
  "Mystery",
  "Adventure",
  "Romance",
  "Classics",
];

const demoRecommendations = [
  {
    recordId: "demo-001",
    title: "The Clockmaker's Secret",
    reason: "A cosy mystery with gentle pacing and an intriguing puzzle at its heart.",
    rank: 1,
  },
  {
    recordId: "demo-002",
    title: "The Small Museum of Lost Things",
    reason: "A warm, character-led story with a touch of humour and discovery.",
    rank: 2,
  },
  {
    recordId: "demo-003",
    title: "Lanterns at Low Tide",
    reason: "An atmospheric read about community, hope and finding your way forward.",
    rank: 3,
  },
];

function AssistantMessage({ children }) {
  return (
    <div className="assistant-message">
      <span className="assistant-message__avatar" aria-hidden="true">
        R
      </span>
      <div>
        <strong>RealSAM Reads</strong>
        <p>{children}</p>
      </div>
    </div>
  );
}

function RecommendationChat({ selectedUser }) {
  const navigate = useNavigate();
  const [message, setMessage] = useState("");
  const [selectedPreferences, setSelectedPreferences] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  function togglePreference(preference) {
    setSelectedPreferences((current) =>
      current.includes(preference)
        ? current.filter((item) => item !== preference)
        : [...current, preference],
    );
  }

  const handleSend = async (event) => {
    event.preventDefault();
    const request = [message.trim(), ...selectedPreferences].filter(Boolean).join(", ");
    if (!request || isLoading) {
      return;
    }

    setError("");
    setIsLoading(true);
    try {
      const response = await fetch(
          `${API_BASE}/chat?userId=${encodeURIComponent(selectedUser.id)}&message=${encodeURIComponent(request)}`
      );
      if (!response.ok) throw new Error("The recommendation service could not complete your request.");

      const data = await response.json();
      if (!Array.isArray(data.recommendations)) {
        throw new Error("The recommendation service returned an unexpected response.");
      }

      navigate("/recommendations", {
        state: { request, recommendations: data.recommendations, userId: selectedUser.id, isDemo: false },
      });
    } catch (error) {
      if (error instanceof TypeError) {
        navigate("/recommendations", {
          state: { request, recommendations: demoRecommendations, userId: selectedUser.id, isDemo: true },
        });
      } else {
        setError(error.message);
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <section
      className="recommendation-chat"
      aria-labelledby="recommendation-title"
    >
      <p className="section-label">Recommendation assistant</p>
      <h1 id="recommendation-title">Let&apos;s find your next book</h1>

      <AssistantMessage>
        Hi! Tell me what you enjoy in your own words. I&apos;ll ask a couple of
        short follow-up questions to understand you better.
      </AssistantMessage>

      <AssistantMessage>
        What kinds of books are you interested in?
      </AssistantMessage>

      <div
        className="preference-suggestions"
        aria-label="Suggested preferences"
      >
        {preferenceSuggestions.map((suggestion) => (
          <button
            type="button"
            key={suggestion}
            className={selectedPreferences.includes(suggestion) ? "is-selected" : ""}
            aria-pressed={selectedPreferences.includes(suggestion)}
            onClick={() => togglePreference(suggestion)}
          >
            {selectedPreferences.includes(suggestion) ? "✓" : "+"} {suggestion}
          </button>
        ))}
      </div>

      <p className="recommendation-chat__hint">
        Choose any tags above, type a natural-language answer below, or use both.
      </p>

      <form className="recommendation-chat__input-row" onSubmit={handleSend}>
        <label className="visually-hidden" htmlFor="book-preferences">
          Describe the books you enjoy
        </label>

        <textarea
          id="book-preferences"
          rows="2"
          placeholder="For example: I enjoy funny mysteries with warm characters"
          value={message}
          onChange={(event) => setMessage(event.target.value)}
        />

        <button
          className="primary-button"
          type="submit"
          disabled={isLoading || (!message.trim() && selectedPreferences.length === 0)}
        >
          {isLoading ? "Finding books…" : "Find books →"}
        </button>
      </form>
      {error && <p className="recommendation-chat__error" role="alert">{error}</p>}
    </section>
  );
}

export default RecommendationChat;
