import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import './Login.css';
import Navbar from './Navbar';
import Footer from './Footer';

function Login() {
  const [loginLink, setLoginLink] = useState('');
  const navigate = useNavigate();
  const location = useLocation();
  const [message, setMessage] = useState('');

  useEffect(() => {
    fetch('/api/login')
      .then((response) => response.text())
      .then((link) => setLoginLink(link))
      .catch((error) => console.error('Error fetching login link:', error));

    if (location.state && location.state.from === '/home') {
      setMessage('Please log in to access the Home page.');
    }
    if (location.state && location.state.from === '/add-playlist') {
      setMessage('Please log in to access the Add Playlist page.');
    }
  }, [location]);

  return (
    <div className='page-container'>
      <Navbar />
      <div className="login-container">
        <div className="login-content">
          
          <div className="welcome-text">
            <h1>Login to Spotify Transfer</h1>
            <p>Transfer your Yandex Music playlists to Spotify in seconds</p>
          </div>
  
          {message && <p className="login-message">{message}</p>}
          
          <ul className="benefits-list">
            <li className="benefit-item">
              <h3>Fast Transfer</h3>
              <p>Quick and easy playlist migration</p>
            </li>
            <li className="benefit-item">
              <h3>Keep Your Music</h3>
              <p>Transfer entire playlists at once</p>
            </li>
            <li className="benefit-item">
              <h3>Smart Matching</h3>
              <p>Accurate song matching system</p>
            </li>
            <li className="benefit-item">
              <h3>Free to Use</h3>
              <p>No hidden costs or limits</p>
            </li>
          </ul>
  
          <a href={loginLink} className="login-link">
            Login with Spotify
          </a>
          <button onClick={() => navigate(-1)} className="back-button">
            Go Back
          </button>
        </div>
      </div>
      <Footer />
    </div>
  );
}

export default Login;