import "./BookCard.css";

function BookCard({ book, progress, saveType = "liked", isSaved, onSaveToggle }) {
  const isRejected = saveType === "rejected";
  const activeIcon = isRejected ? "×" : "★";
  const inactiveIcon = isRejected ? "↶" : "☆";
  const activeLabel = isRejected
    ? `Remove ${book.title} from Not for me`
    : `Remove ${book.title} from liked books`;
  const inactiveLabel = isRejected
    ? `Keep ${book.title} in Not for me`
    : `Keep ${book.title} in liked books`;

  return (
    <article className="book-card">
      {onSaveToggle && (
        <button
          className={`book-card__save-button ${
            isRejected ? "book-card__save-button--rejected" : ""
          }`}
          type="button"
          aria-label={isSaved ? activeLabel : inactiveLabel}
          aria-pressed={isSaved}
          title={isSaved ? "Remove when you leave this page" : "Keep this book"}
          onClick={onSaveToggle}
        >
          <span aria-hidden="true">{isSaved ? activeIcon : inactiveIcon}</span>
        </button>
      )}

      <div
        className="book-card__cover"
        style={{ backgroundColor: book.coverColour }}
        aria-hidden="true"
      >
        <span>A novel</span>
        <div>
          <strong>{book.title}</strong>
          <small>{book.author}</small>
        </div>
      </div>

      <div className="book-card__details">
        <h3>{book.title}</h3>
        <p>{book.author}</p>

        {progress !== undefined && (
          <div className="book-card__progress">
            <div className="book-card__progress-label">
              <span>Reading progress</span>
              <strong>{progress}%</strong>
            </div>
            <progress value={progress} max="100">
              {progress}%
            </progress>
          </div>
        )}
      </div>
    </article>
  );
}

export default BookCard;
