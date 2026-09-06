import { useState } from "react";
import Header from "./components/Header";
import { users } from "./mocks/users";
import "./App.css";

function App() {
  const [selectedUser, setSelectedUser] = useState(users[0]);

  return (
    <>
      <Header
        users={users}
        selectedUser={selectedUser}
        onUserChange={setSelectedUser}
      />

      <main className="main-content">
        <section className="welcome-panel">
          <p className="welcome-panel__eyebrow">
            Welcome, {selectedUser.name}
          </p>

          <h2>Find your next book</h2>

          <p>
            Your current interests: {selectedUser.preferences}
          </p>

          <button type="button" className="primary-button">
            Get recommendations
          </button>
        </section>
      </main>
    </>
  );
}

export default App;