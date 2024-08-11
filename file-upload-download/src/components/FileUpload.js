import React, { useState, useEffect } from 'react';
import './FileUpload.css';

const apiUrl = 'http://localhost:8080/files'; // 서버의 API URL

function FileUpload() {
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

       const messageId = 1; // 임시로 설정한 메시지 ID, 실제 구현에서는 동적으로 설정 필요

               const formData = new FormData();
               formData.append('file', file);
               formData.append('messageId', messageId); // DTO로 전달

        fetch(`${apiUrl}/upload`, {
            method: 'POST',
            body: formData
        })
            .then(response => response.text())
            .then(fileKey => {
                const storageUrl = `${apiUrl}/download/${fileKey}`;
                                addFileToList(file.name, fileKey, storageUrl);
                                saveUploadedFile(file.name, fileKey, storageUrl);
            })
            .catch(error => {
                console.error('Error uploading file:', error);
                alert('Failed to upload file.');
            });
    };

    const loadFileList = () => {
        // API에서 파일 목록을 불러와 상태에 반영
        fetch(`${apiUrl}/list`, {
            method: 'GET'
        }).then(response => {
                      if (!response.ok) {
                          throw new Error('Network response was not ok');
                      }
                      return response.json();
                  }).then(fileList => {
                                    console.log('File list loaded:', fileList); // 파일 목록 로드 로그 추가
                                    const files = fileList.map(file => ({
                                                        fileName: file.fileName,
                                                        fileKey: file.fileKey // 여기서는 S3의 키를 사용
                                                    }));
                                    setUploadedFiles(files);
                                    localStorage.setItem('uploadedFiles', JSON.stringify(files));
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

    const deleteFile = (fileKey) => {
            fetch(`${apiUrl}/delete/${fileKey}`, {
                method: 'DELETE'
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok.');
                    }
                     setUploadedFiles(uploadedFiles.filter(file => file.fileKey !== fileKey));
                })
                .catch(error => {
                console.error('Error deleting file:', error);
                alert('Failed to delete file.');
                });
        };

    const addFileToList = (fileName, fileKey, storageUrl) => {
        setUploadedFiles([...uploadedFiles, { fileName, fileKey, storageUrl }]);
    };

    const addDownloadedFileToList = (fileName) => {
        setDownloadedFiles([...downloadedFiles, { fileName }]);
    };

    const loadDownloadedFileList = () => {
        const downloadedFiles = JSON.parse(localStorage.getItem('downloadedFiles')) || [];
        setDownloadedFiles(downloadedFiles);
    };

    const saveUploadedFile = (fileName, fileKey, storageUrl) => {
        const updatedFiles = [...uploadedFiles, { fileName, fileKey, storageUrl }];
        localStorage.setItem('uploadedFiles', JSON.stringify(updatedFiles));
    };

    const saveDownloadedFile = (fileName) => {
        const updatedFiles = [...downloadedFiles, { fileName }];
        localStorage.setItem('downloadedFiles', JSON.stringify(updatedFiles));
    };

    console.log(uploadedFiles); // 파일 목록이 제대로 로드되는지 확인

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
                        <button onClick={() => deleteFile(file.fileKey)}>Delete 🗑️</button>
                    </div>
                ))}
            </div>
            <h2>Downloaded Files</h2>
            <div id="downloadedFileList">
                {downloadedFiles.map((file, index) => (
                    <div key={index} className="file-item">
                        <span className="icon">📁</span>
                        <span>{file.fileName}</span>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default FileUpload;
