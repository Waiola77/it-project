import { Link } from "react-router-dom";
import "./AccountSidebar.css";

function AccountSidebar({ user }) {
  return (
    <aside className="account-sidebar" aria-labelledby="current-account-title">
      <p className="section-label" id="current-account-title">
        Current account
      </p>
      <div className="account-card">
        <span className="account-card__avatar" aria-hidden="true">
          {user.name.charAt(0)}
        </span>
        <h2>{user.name}</h2>
        <p>{user.preferences}</p>
      </div>
      <Link className="account-sidebar__profile-link" to="/profile">
        View profile &amp; history
      </Link>
    </aside>
  );
}

export default AccountSidebar;
