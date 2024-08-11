import React from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import EmojiApp from './components/EmojiApp';
import FileUpload from './components/FileUpload';
import InputArea from './components/InputArea';
import './index.css';

const container = document.getElementById('root');
const root = createRoot(container);

root.render(
    <React.StrictMode>
        <Router>
            <Routes>
                <Route path="/" element={<EmojiApp />} />
                <Route path="/file-upload" element={<FileUpload />} />
                <Route path="/input" element={<InputArea />} />
            </Routes>
        </Router>
    </React.StrictMode>
);
