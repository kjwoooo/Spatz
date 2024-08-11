import React, { useState, useRef, useEffect } from 'react';
import './InputArea.css';
import 'emoji-picker-element';

const apiUrl = 'http://localhost:8080';

function InputArea() {
    const [message, setMessage] = useState('');
    const [pickerVisible, setPickerVisible] = useState(false);
    const [file, setFile] = useState(null);
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

    const handleFileChange = (event) => {
        const selectedFile = event.target.files[0];
        setFile(event.target.files[0]);
        console.log("Selected file:", event.target.files[0]);
    };

    const extractEmojis = (text) => {
            return text.match(/[\p{Emoji}\u200d]+/gu) || [];
        };

    const handleSendMessage = () => {
       if (!message.trim() && !file) {
                   alert('Please enter a message or select a file.');
                   return;
               }

               const emojis = extractEmojis(message);
               const textContent = message.replace(/[\p{Emoji}\u200d]+/gu, '');

               if (emojis.length > 0) {
                   // 이모지 데이터를 서버에 전송
                   fetch(`${apiUrl}/reactions`, {
                       method: 'POST',
                       headers: {
                           'Content-Type': 'application/json'
                       },
                       body: JSON.stringify({ emojis, messageId: 1 }) // messageId는 예시로 하드코딩, 필요에 따라 수정
                   })
                   .then(response => response.json())
                   .then(data => console.log('Emojis saved:', data))
                   .catch(error => console.error('Error saving emojis:', error));
               }

               if (file) {
                   // 파일을 서버에 업로드
                   const formData = new FormData();
                   formData.append('file', file);
                   formData.append('messageId', 1); // messageId는 예시로 하드코딩, 필요에 따라 수정

                   fetch(`${apiUrl}/files/upload`, {
                       method: 'POST',
                       body: formData
                   })
                   .then(response => response.text())
                   .then(data => {
                       console.log('File uploaded:', data);
                       setFile(null); // 파일 업로드 후 상태 초기화
                   })
                   .catch(error => {
                       console.error('Error uploading file:', error);
                       alert('Failed to upload file.');
                   });
               }
               // 텍스트 부분만 Redis에 저장하도록 메시지를 서버로 전송
               if (textContent.trim()) {
                           fetch(`${apiUrl}/chat/send`, {
                               method: 'POST',
                               headers: {
                                   'Content-Type': 'application/json'
                               },
                               body: JSON.stringify({ content: textContent, messageId: 1 }) // messageId와 content 전달
                           })
                           .then(response => response.json())
                           .then(data => console.log('Text saved:', data))
                           .catch(error => console.error('Error saving text:', error));
               }


               setMessage('');
    };


    return (
        <div className="input-container">
            <input
                type="file"
                id="fileInput"
                style={{ display: 'none' }}
                onChange={handleFileChange}
            />
            <button
                className="file-upload-button"
                onClick={() => document.getElementById('fileInput').click()}
            >
                +
            </button>
            <div className="input-wrapper">
                <input
                    type="text"
                    className="message-input"
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    placeholder="Type a message..."
                />
                <button
                    className="emoji-picker-button"
                    onClick={() => setPickerVisible(!pickerVisible)}
                >
                    😊
                </button>
            </div>

                <div id="pickerContainer" style={{ display: pickerVisible ? 'block' : 'none' }}>
                                <emoji-picker ref={pickerRef}></emoji-picker>
                            </div>
            }
            <button className="send-button" onClick={handleSendMessage}>Send</button>
        </div>
    );
}

export default InputArea;
