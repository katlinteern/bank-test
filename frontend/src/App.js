import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import NavBar from "./components/NavBar/NavBar";
import Investments from "./components/Investments"; 

function App() {
  return (
    <Router>
        <div className="container-fluid">
          <div className="row">
            <div className="col-md-2">
              <NavBar />
            </div>

            <div className="col-md-10">
              <div className="header-container">
                <h2>Portfolio</h2>
              </div>
              <Routes>
                <Route path="/" element={<Investments />} />
              </Routes>
            </div>
          </div>
        </div>
    </Router>
  );
}

export default App;
