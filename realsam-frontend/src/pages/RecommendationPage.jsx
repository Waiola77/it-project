import { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import Header from "../components/Header";
import "./RecommendationPage.css";

function RecommendationPage({ users, selectedUser, onUserChange }) {
  const { state } = useLocation();
  const hasResults = state?.userId === selectedUser.id && Array.isArray(state?.recommendations);
  const [allRecommendations] = useState(() =>
    hasResults ? state.recommendations : [],
  );

  const [recommendations, setRecommendations] = useState(() =>
    hasResults ? state.recommendations.slice(0, 3) : [],
  );

  const [nextRecommendationIndex, setNextRecommendationIndex] = useState(3);
  const [feedbackByBook, setFeedbackByBook] = useState({});
  const [feedbackNotice, setFeedbackNotice] = useState("");
  const [isReplacing, setIsReplacing] = useState(false);
  const [selectedBookDetails, setSelectedBookDetails] = useState(null);

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
        const replacement =
          nextRecommendationIndex < allRecommendations.length
            ? allRecommendations[nextRecommendationIndex]
            : null;

        setRecommendations((current) => {
          const updated = current.filter(
            (recommendation) => recommendation.recordId !== bookId,
          );

          if (replacement) {
            updated.push(replacement);
          }

          return updated;
        });

        if (replacement) {
          setNextRecommendationIndex((currentIndex) => currentIndex + 1);

          setFeedbackNotice(
            `${book.title} was removed and the next recommendation was added.`,
          );
        } else {
          setFeedbackNotice(
            `${book.title} was removed. No more recommendations are available.`,
          );
        }
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
                <div
                  className="recommendation-book__number"
                  aria-label={`Recommendation ${index + 1}`}
                >
                  {String(index + 1).padStart(2, "0")}
                </div>
                <div className="recommendation-book__content">
                  <p className="recommendation-book__eyebrow">Recommended book</p>
                  <h2>{book.title || "Untitled book"}</h2>
                  <div className="recommendation-book__reason">
                    <strong>Why this book?</strong>
                    <p>{book.reason || "No recommendation reason provided."}</p>
                  </div>
                  <button
                    className="recommendation-book__details-button"
                    type="button"
                    onClick={() => setSelectedBookDetails(book)}
                  >
                    View book details
                  </button>
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
      {selectedBookDetails && (
        <div
          className="book-modal__overlay"
          role="presentation"
          onClick={() => setSelectedBookDetails(null)}
        >
          <div
            className="book-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="book-modal-title"
            onClick={(event) => event.stopPropagation()}
          >
            <button
              className="book-modal__close"
              type="button"
              aria-label="Close book details"
              onClick={() => setSelectedBookDetails(null)}
            >
              ×
            </button>

            <p className="section-label">Book details</p>

            <h2 id="book-modal-title">
              {selectedBookDetails.title || "Untitled book"}
            </h2>

            {selectedBookDetails.author && (
              <p className="book-modal__author">
                by {selectedBookDetails.author}
              </p>
            )}

            <div className="book-modal__description">
              <strong>About this book</strong>
              <p>
                {selectedBookDetails.description ||
                  "No description is available for this book."}
              </p>
            </div>

            {selectedBookDetails.recordId && (
              <p className="book-modal__record-id">
                Catalogue ID: {selectedBookDetails.recordId}
              </p>
            )}
          </div>
        </div>
      )}
    </>
  );
}

export default RecommendationPage;
