import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import './App.css';
import Navbar from './Navbar';
import Footer from './Footer';

function App() {
  return (
    <div className="page-container">
      <Navbar />
      <div className="container">
        <div className="image-space">
          <img src="/yts.jpg" alt="Music Transfer" />
        </div>
        <div className="content-box">
          <div className="welcome-text">
            <h1>Welcome to Spotify Transfer App</h1>
          </div>

          <div className="features-guide-container">
            <div className="features-box">
              <h3>Features:</h3>
              <ul>
                <li>✓ Transfer YM playlists to Spotify</li>
                <li>✓ Fast and reliable song matching</li>
                <li>✓ Keep your music organized</li>
                <li>✓ Simple one-click transfer</li>
              </ul>
            </div>
            <div className="steps-box">
              <h3>How it works:</h3>
              <ul>
                <li>Login with your Spotify account</li>
                <li>Copy your YM playlist link</li>
                <li>Name your new playlist</li>
                <li>Click transfer and enjoy!</li>
              </ul>
            </div>
          </div>
        </div>
        <a href="/login" className="login-button">
          Get Started Now
        </a>
      </div>
      <Footer />
    </div>
  );
}

export default App;