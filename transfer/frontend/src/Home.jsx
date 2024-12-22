import { useEffect, useState } from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import Navbar from './Navbar';
import Footer from './Footer';
import './Home.css';

function Home() {
  const [message, setMessage] = useState('');
  const [avatarUrl, setAvatarUrl] = useState('');
  const location = useLocation();
  const navigate = useNavigate();
  const searchParams = new URLSearchParams(location.search);
  const userId = searchParams.get('id');

  useEffect(() => {
    if (!userId) {
      navigate('/login', { state: { from: '/home' } });
    } else {
      fetch(`/api/home?id=${userId}`)
        .then((response) => response.text())
        .then((data) => setMessage(data))
        .catch((error) => console.error('Error fetching message:', error));
    }
  }, [userId, navigate]);

  useEffect(() => {
    if (userId) {
      fetch(`/api/user-avatar?id=${userId}`)
        .then(res => res.text())
        .then(url => setAvatarUrl(url))
        .catch(error => console.error('Error fetching avatar:', error));
    }
  }, [userId]);

  return (
    <div className="page-container">
      <Navbar />
      <div className="home-content">
        <div className="home-box">
          <h1>{message}</h1>
          <div className="user-avatar">
            {avatarUrl && <img src={avatarUrl} alt="User Profile" />}
          </div>
          <div className="action-buttons">
            <Link to={`/add-playlist?id=${userId}`} className="add-playlist-button">
              Transfer New Playlist
            </Link>
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
}

export default Home;