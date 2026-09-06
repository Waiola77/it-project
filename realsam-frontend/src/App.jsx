import { useState } from "react";
import { Route, Routes } from "react-router-dom";
import HomePage from "./pages/HomePage";
import ProfilePage from "./pages/ProfilePage";
import { profileActivityByUser } from "./mocks/profileActivity";
import { users } from "./mocks/users";
import "./App.css";

function App() {
  const [selectedUser, setSelectedUser] = useState(users[0]);
  const [profileActivity, setProfileActivity] = useState(profileActivityByUser);

  function handleSavedBookChange(userId, collection, bookId, isSaved) {
    setProfileActivity((currentActivity) => {
      const userActivity = currentActivity[userId];
      const currentBooks = userActivity[collection];
      const updatedBooks = isSaved
        ? [...currentBooks, bookId]
        : currentBooks.filter((id) => id !== bookId);

      return {
        ...currentActivity,
        [userId]: {
          ...userActivity,
          [collection]: updatedBooks,
        },
      };
    });
  }

  return (
    <Routes>
      <Route
        path="/"
        element={
          <HomePage
            users={users}
            selectedUser={selectedUser}
            onUserChange={setSelectedUser}
          />
        }
      />
      <Route
        path="/profile"
        element={
          <ProfilePage
            selectedUser={selectedUser}
            activity={profileActivity[selectedUser.id]}
            onSavedBookChange={handleSavedBookChange}
          />
        }
      />
    </Routes>
  );
}

export default App;
