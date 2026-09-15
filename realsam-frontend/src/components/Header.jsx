import "./Header.css";
import realSamLogo from "../assets/realsam-logo.jpg";
import UserMenu from "./UserMenu";

function Header({ users, selectedUser, onUserChange }) {
  return (
    <header className="header">
      <div className="header__identity">
        <img
          className="header__logo"
          src={realSamLogo}
          alt="RealSAM logo"
        />

        <div className="header__text">
          <p className="header__brand">RealSAM</p>
          <h1 className="header__title">Book Recommendations</h1>
        </div>
      </div>

      <UserMenu
        users={users}
        selectedUser={selectedUser}
        onUserChange={onUserChange}
      />
    </header>
  );
}

export default Header;
