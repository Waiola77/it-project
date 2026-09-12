import { useState } from "react";
import "./UserMenu.css";

function UserMenu({ users, selectedUser, onUserChange }) {
  const [isOpen, setIsOpen] = useState(false);

  function handleUserSelect(user) {
    onUserChange(user);
    setIsOpen(false);
  }

  return (
    <div className="user-menu">
      <button
        className="user-menu__button"
        type="button"
        aria-expanded={isOpen}
        aria-haspopup="menu"
        onClick={() => setIsOpen(!isOpen)}
      >
        <span className="user-menu__avatar">
          {selectedUser.name.charAt(0)}
        </span>

        <span className="user-menu__details">
          <span className="user-menu__label">Current user</span>
          <span className="user-menu__name">{selectedUser.name}</span>
        </span>

        <span aria-hidden="true" className="user-menu__arrow">
          {isOpen ? "▲" : "▼"}
        </span>
      </button>

      {isOpen && (
        <div className="user-menu__dropdown" role="menu">
          <p className="user-menu__heading">Switch account</p>

          {users.map((user) => (
            <button
              className={`user-menu__option ${
                selectedUser.id === user.id
                  ? "user-menu__option--selected"
                  : ""
              }`}
              type="button"
              role="menuitemradio"
              aria-checked={selectedUser.id === user.id}
              key={user.id}
              onClick={() => handleUserSelect(user)}
            >
              <span className="user-menu__avatar">
                {user.name.charAt(0)}
              </span>

              <span className="user-menu__option-text">
                <strong>{user.name}</strong>
                <small>{user.preferences}</small>
              </span>
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

export default UserMenu;