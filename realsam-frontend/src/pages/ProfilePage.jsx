import { useState } from "react";
import { Link } from "react-router-dom";
import BookCard from "../components/BookCard";
import { books } from "../mocks/profileActivity";
import "./ProfilePage.css";

const tabs = [
  { id: "liked", label: "Liked books" },
  { id: "rejected", label: "Not for me" },
  { id: "read", label: "Books read" },
];

const emptyActivity = { liked: [], rejected: [], read: [] };

function getBooksForTab(activity, activeTab) {
  if (activeTab === "read") {
    return activity.read.map(({ bookId, progress }) => ({
      ...books[bookId],
      progress,
    }));
  }

  return activity[activeTab].map((bookId) => books[bookId]);
}

function ProfilePage({ selectedUser, activity = emptyActivity, onSavedBookChange }) {
  const [activeTab, setActiveTab] = useState("read");
  const [activitySnapshot] = useState(() => activity);
  const visibleBooks = getBooksForTab(activitySnapshot, activeTab);

  function handleSaveToggle(bookId) {
    const isCurrentlySaved = activity[activeTab].includes(bookId);
    onSavedBookChange(
      selectedUser.id,
      activeTab,
      bookId,
      !isCurrentlySaved,
    );
  }

  return (
    <main className="profile-page">
      <Link className="profile-page__back-link" to="/">
        ← Back to recommendations
      </Link>

      <section className="profile-hero" aria-labelledby="profile-name">
        <span className="profile-hero__avatar" aria-hidden="true">
          {selectedUser.name.charAt(0)}
        </span>
        <div>
          <p>My profile</p>
          <h1 id="profile-name">{selectedUser.name}</h1>
          <span>{selectedUser.preferences}</span>
        </div>
      </section>

      <section className="reading-activity" aria-labelledby="reading-activity-title">
        <h2 className="visually-hidden" id="reading-activity-title">
          Reading activity
        </h2>

        <div className="activity-tabs" role="tablist" aria-label="Reading activity">
          {tabs.map((tab) => (
            <button
              className={activeTab === tab.id ? "activity-tab activity-tab--active" : "activity-tab"}
              type="button"
              role="tab"
              aria-selected={activeTab === tab.id}
              aria-controls="activity-panel"
              id={`activity-tab-${tab.id}`}
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
            >
              {tab.label}
              <span>{activitySnapshot[tab.id].length}</span>
            </button>
          ))}
        </div>

        <div
          className="activity-panel"
          id="activity-panel"
          role="tabpanel"
          aria-labelledby={`activity-tab-${activeTab}`}
        >
          {visibleBooks.length > 0 ? (
            <div className="book-grid">
              {visibleBooks.map((book) => (
                <BookCard
                  book={book}
                  progress={book.progress}
                  saveType={activeTab === "rejected" ? "rejected" : "liked"}
                  isSaved={
                    activeTab === "read" || activity[activeTab].includes(book.id)
                  }
                  onSaveToggle={
                    activeTab === "read" ? undefined : () => handleSaveToggle(book.id)
                  }
                  key={book.id}
                />
              ))}
            </div>
          ) : (
            <p className="activity-panel__empty">
              There are no books in this section yet.
            </p>
          )}
        </div>
      </section>
    </main>
  );
}

export default ProfilePage;
