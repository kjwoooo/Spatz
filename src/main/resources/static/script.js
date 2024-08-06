import React, { useState, useEffect } from 'react';
import './App.css';

const apiUrl = 'http://localhost:8080/files';

function Script() {
    const [file, setFile] = useState(null);
    const [uploadedFiles, setUploadedFiles] = useState([]);
    const [downloadedFiles, setDownloadedFiles] = useState([]);

    useEffect(() => {
        loadFileList();
        loadDownloadedFileList();
    }, []);

    const handleFileChange = (event) => {
        setFile(event.target.files[0]);
    };

    const uploadFile = () => {
        if (!file) {
            alert('Please select a file.');
            return;
        }

        const formData = new FormData();
        formData.append('file', file);

        fetch(`${apiUrl}/upload`, {
            method: 'POST',
            body: formData
        })
            .then(response => response.text())
            .then(fileKey => {
                addFileToList(file.name, fileKey);
                saveUploadedFile(file.name, fileKey);
            })
            .catch(error => {
                console.error('Error uploading file:', error);
                alert('Failed to upload file.');
            });
    };

    const loadFileList = () => {
        fetch(`${apiUrl}/list`, {
            method: 'GET'
        })
            .then(response => response.json())
            .then(fileList => {
                setUploadedFiles(fileList);
                localStorage.setItem('uploadedFiles', JSON.stringify(fileList));
            })
            .catch(error => {
                console.error('Error loading file list:', error);
            });
    };

    const downloadFile = (fileKey, fileName) => {
        fetch(`${apiUrl}/download/${fileKey}`, {
            method: 'GET'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok.');
                }
                return response.blob();
            })
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = fileName;
                document.body.appendChild(a);
                a.click();
                a.remove();
                window.URL.revokeObjectURL(url);

                addDownloadedFileToList(fileName);
                saveDownloadedFile(fileName);
            })
            .catch(error => {
                console.error('Error downloading file:', error);
                alert('Failed to download file.');
            });
    };

    const addFileToList = (fileName, fileKey) => {
        setUploadedFiles([...uploadedFiles, { fileName, fileKey }]);
    };

    const addDownloadedFileToList = (fileName) => {
        setDownloadedFiles([...downloadedFiles, fileName]);
    };

    const loadDownloadedFileList = () => {
        const downloadedFiles = JSON.parse(localStorage.getItem('downloadedFiles')) || [];
        setDownloadedFiles(downloadedFiles);
    };

    const saveUploadedFile = (fileName, fileKey) => {
        const updatedFiles = [...uploadedFiles, { fileName, fileKey }];
        localStorage.setItem('uploadedFiles', JSON.stringify(updatedFiles));
    };

    const saveDownloadedFile = (fileName) => {
        const updatedFiles = [...downloadedFiles, fileName];
        localStorage.setItem('downloadedFiles', JSON.stringify(updatedFiles));
    };

    return (
        <div className="App">
            <h1>File Upload and Download</h1>
            <div>
                <input type="file" onChange={handleFileChange} />
                <button onClick={uploadFile}>Upload</button>
            </div>
            <h2>Uploaded Files</h2>
            <div id="fileList">
                {uploadedFiles.map(file => (
                    <div key={file.fileKey} className="file-item">
                        <span className="icon">📁</span>
                        <span>{file.fileName}</span>
                        <button onClick={() => downloadFile(file.fileKey, file.fileName)}>Download 📥</button>
                    </div>
                ))}
            </div>
            <h2>Downloaded Files</h2>
            <div id="downloadedFileList">
                {downloadedFiles.map((fileName, index) => (
                    <div key={index} className="file-item">
                        <span className="icon">📁</span>
                        <span>{fileName}</span>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default Script;
