import { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import Header from "../components/Header";
import "./RecommendationPage.css";

function RecommendationPage({ users, selectedUser, onUserChange }) {
  const { state } = useLocation();
  const hasResults = state?.userId === selectedUser.id && Array.isArray(state?.recommendations);
  const [recommendations, setRecommendations] = useState(() =>
    hasResults ? state.recommendations : [],
  );
  const [feedbackByBook, setFeedbackByBook] = useState({});
  const [feedbackNotice, setFeedbackNotice] = useState("");
  const [isReplacing, setIsReplacing] = useState(false);

  async function handleFeedback(book, feedbackType) {
    const bookId = book.recordId;
    let feedbackSaved = false;
    if (!bookId || feedbackByBook[bookId] === feedbackType) {
      return;
    }

    setFeedbackByBook((current) => ({ ...current, [bookId]: feedbackType }));
    setFeedbackNotice(
      feedbackType === "liked"
        ? `${book.title} added to your liked books.`
        : `${book.title} marked as Not for me. Finding another book…`,
    );

    if (feedbackType === "rejected") {
      setIsReplacing(true);
    }

    try {
      const response = await fetch(
        `http://localhost:8080/feedback/${feedbackType === "liked" ? "like" : "reject"}?userId=${encodeURIComponent(selectedUser.id)}&bookId=${encodeURIComponent(bookId)}`,
        { method: "POST" },
      );

      if (!response.ok) {
        throw new Error("Feedback could not be saved");
      }
      feedbackSaved = true;

      if (feedbackType === "rejected") {
        setRecommendations((current) =>
          current.filter((recommendation) => recommendation.recordId !== bookId),
        );
        const replacementResponse = await fetch(
          `http://localhost:8080/chat?userId=${encodeURIComponent(selectedUser.id)}&message=${encodeURIComponent(state.request)}`,
        );

        if (!replacementResponse.ok) {
          throw new Error("A replacement recommendation could not be loaded");
        }

        const replacementData = await replacementResponse.json();
        if (!Array.isArray(replacementData.recommendations)) {
          throw new Error("The recommendation service returned an unexpected response");
        }

        setRecommendations(replacementData.recommendations);
        setFeedbackNotice(`${book.title} was removed and a new recommendation was added.`);
      }
    } catch {
      if (feedbackType === "rejected" && !feedbackSaved) {
        setFeedbackByBook((current) => {
          const updated = { ...current };
          delete updated[bookId];
          return updated;
        });
      }
      setFeedbackNotice(
        feedbackType === "rejected"
          ? feedbackSaved
            ? `${book.title} was removed, but a replacement could not be loaded.`
            : `${book.title} could not be removed because the recommendation service is offline.`
          : `${book.title} is marked for this session. The service is currently offline.`,
      );
    } finally {
      if (feedbackType === "rejected") {
        setIsReplacing(false);
      }
    }
  }

  return (
    <>
      <Header users={users} selectedUser={selectedUser} onUserChange={onUserChange} />
      <main className="recommendation-page">
        <Link className="recommendation-page__back" to="/">← Back to chat</Link>

        <section className="recommendation-page__hero" aria-labelledby="results-title">
          <p className="section-label">Your next read</p>
          <h1 id="results-title">Books picked for you</h1>
          {state?.isDemo && hasResults && (
            <p className="recommendation-page__demo-note">Demo preview · Example books shown while the recommendation service is offline.</p>
          )}
          {hasResults ? (
            <p>Based on <strong>“{state.request}”</strong>, here are your personalised recommendations.</p>
          ) : (
            <p>Tell us what you like in the chat to get recommendations made for you.</p>
          )}
        </section>

        {recommendations.length > 0 ? (
          <section className="recommendation-page__list" aria-label="Recommended books">
            {recommendations.map((book, index) => (
              <article className="recommendation-book" key={book.recordId || `${book.title}-${index}`}>
                <div className="recommendation-book__number" aria-label={`Rank ${book.rank || index + 1}`}>
                  {String(book.rank || index + 1).padStart(2, "0")}
                </div>
                <div className="recommendation-book__content">
                  <p className="recommendation-book__eyebrow">Recommended book</p>
                  <h2>{book.title || "Untitled book"}</h2>
                  <div className="recommendation-book__reason">
                    <strong>Why this book?</strong>
                    <p>{book.reason || "No recommendation reason provided."}</p>
                  </div>
                  {book.recordId && <p className="recommendation-book__record-id">Catalogue ID: {book.recordId}</p>}
                  {book.recordId && (
                    <div className="recommendation-book__actions" aria-label={`Rate ${book.title}`}>
                      <button
                        className={feedbackByBook[book.recordId] === "liked" ? "feedback-button feedback-button--like is-selected" : "feedback-button feedback-button--like"}
                        type="button"
                        disabled={isReplacing}
                        aria-pressed={feedbackByBook[book.recordId] === "liked"}
                        onClick={() => handleFeedback(book, "liked")}
                      >
                        <span aria-hidden="true">
                          {feedbackByBook[book.recordId] === "liked" ? "★" : "☆"}
                        </span>{" "}
                        Like
                      </button>
                      <button
                        className={feedbackByBook[book.recordId] === "rejected" ? "feedback-button feedback-button--reject is-selected" : "feedback-button feedback-button--reject"}
                        type="button"
                        disabled={isReplacing}
                        aria-pressed={feedbackByBook[book.recordId] === "rejected"}
                        onClick={() => handleFeedback(book, "rejected")}
                      >
                        <span aria-hidden="true">×</span> Not for me
                      </button>
                    </div>
                  )}
                </div>
              </article>
            ))}
          </section>
        ) : (
          <div className="recommendation-page__empty">
            <h2>{hasResults ? "No matches yet" : "Start with the chat"}</h2>
            <p>{hasResults
              ? "Try describing a different genre, mood or kind of story."
              : "Your results will appear here after you submit your preferences."}</p>
            <Link className="primary-button" to="/">{hasResults ? "Try another request" : "Find books"}</Link>
          </div>
        )}

        {recommendations.length > 0 && (
          <>
            <p className="recommendation-page__feedback-notice" role="status" aria-live="polite">
              {feedbackNotice}
            </p>
            <Link className="recommendation-page__again" to="/">↻ Try another request</Link>
          </>
        )}
      </main>
    </>
  );
}

export default RecommendationPage;
