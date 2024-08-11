import React, { useState, useRef, useEffect } from 'react';
import './EmojiApp.css';
import 'emoji-picker-element';

const apiUrl = 'http://localhost:8080/reactions'; // 서버의 API URL

function EmojiApp() {
    const [message, setMessage] = useState('');
    const [reactions, setReactions] = useState([]);
    const [pickerVisible, setPickerVisible] = useState(false);
    const pickerRef = useRef(null);

    useEffect(() => {
        const picker = pickerRef.current;
        const handleEmojiClick = (event) => {
            setMessage(message + event.detail.unicode);
            setPickerVisible(false);
        };
        if (picker) {
            picker.addEventListener('emoji-click', handleEmojiClick);
        }
        return () => {
            if (picker) {
                picker.removeEventListener('emoji-click', handleEmojiClick);
            }
        };
    }, [message]);

    useEffect(() => {
        loadReactions();
    }, []);

    const handleAddReaction = () => {
        if (!message) {
            alert('Please enter a message.');
            return;
        }

        const messageId = "1"; // 임시로 설정한 메시지 ID, 실제 구현에서는 동적으로 설정 필요
        fetch(`${apiUrl}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `messageId=${messageId}&emoji=${encodeURIComponent(message)}`
        })
            .then(response => response.json())
            .then(reaction => {
                setReactions([...reactions, reaction]);
                setMessage('');
            })
            .catch(error => {
                console.error('Error adding reaction:', error);
                alert('Failed to add reaction.');
            });
    };

    const handleDeleteReaction = (id, index) => {
        fetch(`${apiUrl}/${id}`, {
            method: 'DELETE'
        })
            .then(() => {
                const newReactions = reactions.filter((_, i) => i !== index);
                setReactions(newReactions);
            })
            .catch(error => {
                console.error('Error deleting reaction:', error);
                alert('Failed to delete reaction.');
            });
    };

    const loadReactions = () => {
        const messageId = "1"; // 임시로 설정한 메시지 ID, 실제 구현에서는 동적으로 설정 필요
        fetch(`${apiUrl}/${messageId}`, {
            method: 'GET'
        })
            .then(response => response.json())
            .then(data => {
                setReactions(data);
            })
            .catch(error => {
                console.error('Error loading reactions:', error);
            });
    };

    return (
        <div className="container">
            <h1>Emoji Test</h1>
            <div id="reactionList">
                {reactions.map((reaction, index) => (
                    <div key={index} className="reaction">
                        <span className="reaction-content">{reaction.emoji}</span>
                        <button className="delete-button" onClick={() => handleDeleteReaction(reaction.id, index)}>Delete</button>
                    </div>
                ))}
            </div>
            <div className="input-container">
                <input
                    type="text"
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    placeholder="Type a message"
                />
                <button onClick={() => setPickerVisible(!pickerVisible)}>😊</button>
            </div>
            <div id="pickerContainer" style={{ display: pickerVisible ? 'block' : 'none' }}>
                <emoji-picker ref={pickerRef}></emoji-picker>
            </div>
            <button onClick={handleAddReaction}>Add</button>
        </div>
    );
}

export default EmojiApp;
