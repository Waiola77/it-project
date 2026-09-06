import Header from "../components/Header";
import AccountSidebar from "../components/AccountSidebar";
import RecommendationChat from "../components/RecommendationChat";
import "./HomePage.css";

function HomePage({ users, selectedUser, onUserChange }) {
  return (
    <>
      <Header
        users={users}
        selectedUser={selectedUser}
        onUserChange={onUserChange}
      />
      <main className="home-layout">
        <AccountSidebar user={selectedUser} />
        <RecommendationChat />
      </main>
    </>
  );
}

export default HomePage;
