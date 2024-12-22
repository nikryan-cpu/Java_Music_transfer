import { useState } from 'react';
import { useLocation } from 'react-router-dom';
import Navbar from './Navbar';
import Footer from './Footer';
import Loader from './Loader';
import './AddPlaylist.css';

function AddPlaylist() {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const userId = searchParams.get('id');

  const [playlistName, setPlaylistName] = useState('');
  const [playlistLink, setPlaylistLink] = useState('');

  const [yandexLink, setYandexLink] = useState('');
  const [existingSpotifyLink, setExistingSpotifyLink] = useState('');

  const [showFormats, setShowFormats] = useState(false);

  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [showOverlay, setShowOverlay] = useState(false);

  const handleSubmitNewPlaylist = (e) => {
    e.preventDefault();
    if (!playlistName.trim() || !playlistLink.trim()) {
      setMessage('Please enter both playlist name and Yandex playlist link.');
      setShowOverlay(true);
      return;
    }
    setLoading(true);

    fetch(
      `/api/add-playlist?playlistName=${encodeURIComponent(playlistName)}&playlistLink=${encodeURIComponent(
        playlistLink
      )}&id=${userId}`,
      { method: 'POST' }
    )
      .then((res) => res.text())
      .then((data) => {
        setLoading(false);
        setMessage(data);
        setShowOverlay(true);
      })
      .catch((error) => {
        setLoading(false);
        setMessage('Error adding playlist.');
        setShowOverlay(true);
        console.error(error);
      });
  };

  const handleAddToExisting = (e) => {
    e.preventDefault();
    if (!yandexLink.trim() || !existingSpotifyLink.trim()) {
      setMessage('Please enter both Yandex and existing Spotify playlist links.');
      setShowOverlay(true);
      return;
    }
    setLoading(true);

    fetch(
      `/api/add-to-existing?yandexLink=${encodeURIComponent(yandexLink)}&spotifyLink=${encodeURIComponent(
        existingSpotifyLink
      )}&id=${userId}`,
      { method: 'POST' }
    )
      .then((res) => res.text())
      .then((data) => {
        setLoading(false);
        setMessage(data);
        setShowOverlay(true);
      })
      .catch((error) => {
        setLoading(false);
        setMessage('Error adding songs to existing playlist.');
        setShowOverlay(true);
        console.error(error);
      });
  };

  const closeOverlay = () => setShowOverlay(false);

  return (
    <div className="page-container">
      {showOverlay && (
      <div className="overlay">
        <div className="overlay-content">
          <h3>
            {message.includes('Error')
              ? 'Error while transferring, check again for valid input'
              : 'Transfer successful!'}
          </h3>
          <p>{message}</p>
          <button onClick={closeOverlay}>Close</button>
        </div>
      </div>
      )}
      <Navbar />
      <div className="add-song-container">
        {loading ? (
          <Loader />
        ) : (
          <>
            <h2>Create a New Spotify Playlist</h2>
            <div className="instructions">
              <h4>How to get your Yandex Music playlist link:</h4>
              <ol>
                <li>Open Yandex Music in your browser</li>
                <li>Navigate to your playlist</li>
                <li>Copy the URL from your browser</li>
              </ol>
            </div>
            <form onSubmit={handleSubmitNewPlaylist}>
              <input
                type="text"
                placeholder="Enter playlist name"
                value={playlistName}
                onChange={(e) => setPlaylistName(e.target.value)}
              />
              <input
                type="text"
                placeholder="Enter Yandex playlist link"
                value={playlistLink}
                onChange={(e) => setPlaylistLink(e.target.value)}
              />
              <button type="submit">Create Playlist</button>
            </form>

            <hr style={{ margin: '30px 0', width: '80%' }} />

            <h2>Add Songs to Existing Spotify Playlist</h2>
            <form onSubmit={handleAddToExisting}>
              <input
                type="text"
                placeholder="Enter Yandex playlist link"
                value={yandexLink}
                onChange={(e) => setYandexLink(e.target.value)}
              />
              <input
                type="text"
                placeholder="Enter existing Spotify playlist link"
                value={existingSpotifyLink}
                onChange={(e) => setExistingSpotifyLink(e.target.value)}
              />
              <button type="submit">Add to Existing</button>
            </form>
            <div className="supported-links">
              <button 
                className="dropdown-button" 
                onClick={() => setShowFormats(!showFormats)}
              >
              Supported Formats {showFormats ? '▲' : '▼'}
              </button>
              <div className={`formats-dropdown ${showFormats ? 'show' : ''}`}>
                <code>https://music.yandex.ru/playlist/...</code>
                <code>https://music.yandex.ru/users/.../playlists/...</code>
                <code>https://music.yandex.ru/album/...</code>
                <code>https://music.yandex.ru/artist/.../albums/...</code>
                <code>https://music.yandex.ru/label/.../albums/...</code>
                <code>https://music.yandex.ru/users/.../albums/...</code>
                <code>https://music.yandex.com/playlist/...</code>
                <code>https://music.yandex.com/users/.../playlists/...</code>
                <code>https://music.yandex.com/album/...</code>
                <code>https://music.yandex.com/artist/.../albums/...</code>
                <code>https://music.yandex.com/label/.../albums/...</code>
                <code>https://music.yandex.com/users/.../albums/...</code>
              </div>
            </div>
          </>
        )}
      </div>
      <Footer />
    </div>
  );
}

export default AddPlaylist;