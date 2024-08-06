import React, { useState, useRef, useEffect } from 'react';
import 'emoji-picker-element';

function App() {
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

    const handleAddReaction = () => {
        if (!message) {
            alert('Please enter a message.');
            return;
        }
        setReactions([...reactions, message]);
        setMessage('');
    };

    const handleDeleteReaction = (index) => {
        const newReactions = reactions.filter((_, i) => i !== index);
        setReactions(newReactions);
    };

    return (
        <div className="container">
            <h1>Emoji Test</h1>
            <div id="reactionList">
                {reactions.map((reaction, index) => (
                    <div key={index} className="reaction">
                        <span className="reaction-content">{reaction}</span>
                        <button className="delete-button" onClick={() => handleDeleteReaction(index)}>Delete</button>
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

export default App;
