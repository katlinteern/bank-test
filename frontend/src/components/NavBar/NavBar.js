import { Link } from "react-scroll";
import './NavBar.css';

export default function NavBar() {
  return (
    <nav className="navbar navbar-vertical navbar-dark bg-custom">
      <div className="container-fluid">
        <div className="navbar-collapse">
          <ul className="navbar-nav flex-column">
            <li className="nav-item">
              <Link
                className="nav-link"
                to="summary"
                smooth={true}
                duration={100}
              >
                Summary
              </Link>
            </li>
            <li className="nav-item">
              <Link
                className="nav-link"
                to="chart"
                smooth={true}
                duration={100}
              >
                Overview
              </Link>
            </li>
            <li className="nav-item">
              <Link
                className="nav-link"
                to="details"
                smooth={true}
                duration={100}
              >
                Details
              </Link>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
}
