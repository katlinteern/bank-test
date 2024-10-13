import { NavLink } from "react-router-dom";
import './NavBar.css'; 

export default function NavBar() {
  return (
    <nav className="navbar navbar-vertical navbar-dark bg-custom"> {/* Use custom class for vertical navbar */}
      <div className="container-fluid">
        <div className="navbar-collapse">
          <ul className="navbar-nav flex-column"> {/* Use flex-column for vertical layout */}
            <li className="nav-item">
              <NavLink className="nav-link" aria-current="page" to="/">List</NavLink>
            </li>
            <li className="nav-item">
              <NavLink className="nav-link" to="/new">Add</NavLink>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
}
