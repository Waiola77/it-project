import "./RecommendationChat.css";

const preferenceSuggestions = [
  "Gentle stories",
  "Funny",
  "Mystery",
  "Adventure",
  "Romance",
  "Classics",
];

function AssistantMessage({ children }) {
  return (
    <div className="assistant-message">
      <span className="assistant-message__avatar" aria-hidden="true">R</span>
      <div>
        <strong>RealSAM Reads</strong>
        <p>{children}</p>
      </div>
    </div>
  );
}

function RecommendationChat() {
  return (
    <section className="recommendation-chat" aria-labelledby="recommendation-title">
      <p className="section-label">Recommendation assistant</p>
      <h1 id="recommendation-title">Let&apos;s find your next book</h1>

      <AssistantMessage>
        Hi! Tell me what you enjoy in your own words. I&apos;ll ask a couple of
        short follow-up questions to understand you better.
      </AssistantMessage>
      <AssistantMessage>What kinds of books are you interested in?</AssistantMessage>

      <div className="preference-suggestions" aria-label="Suggested preferences">
        {preferenceSuggestions.map((suggestion) => (
          <button type="button" key={suggestion}>+ {suggestion}</button>
        ))}
      </div>

      <p className="recommendation-chat__hint">
        Choose any tags above, type a natural-language answer below, or use both.
      </p>

      <div className="recommendation-chat__input-row">
        <label className="visually-hidden" htmlFor="book-preferences">
          Describe the books you enjoy
        </label>
        <textarea
          id="book-preferences"
          rows="2"
          placeholder="For example: I enjoy funny mysteries with warm characters"
        />
        <button className="primary-button" type="button">Send →</button>
      </div>
    </section>
  );
}

export default RecommendationChat;
