import { Link, useLocation, useNavigate } from 'react-router-dom';
import './Navbar.css';

function Navbar() {
  const location = useLocation();
  const navigate = useNavigate();
  const searchParams = new URLSearchParams(location.search);
  const userId = searchParams.get('id');

  const getLink = (path) => (userId ? `${path}?id=${userId}` : path);

  const handleLogout = () => {
    navigate('/', { replace: true });
  };

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <div className="navbar-logo">
          <img src="/logo.svg" alt="YtS Logo" />
        </div>
        <ul className="navbar-links">
          <li><Link to={getLink('/')}>Welcome</Link></li>
          <li><Link to={getLink('/home')}>Home</Link></li>
          <li><Link to={getLink('/add-playlist')}>Add Playlist</Link></li>
        </ul>
      </div>
      <div className="navbar-right">
        {userId ? (
          <Link to="/" onClick={handleLogout} className="auth-link">Logout</Link>
        ) : (
          <Link to="/login" className="auth-link">Login</Link>
        )}
      </div>
    </nav>
  );
}

export default Navbar;