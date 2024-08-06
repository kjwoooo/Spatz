const apiUrl = 'http://localhost:8080/reactions'; // 서버의 API URL

document.addEventListener('DOMContentLoaded', () => {
    const emojiPicker = document.querySelector('emoji-picker');
    emojiPicker.addEventListener('emoji-click', event => {
        const input = document.getElementById('messageInput');
        input.value += event.detail.unicode;
    });
    loadReactions();
});

function addReaction() {
    const messageInput = document.getElementById('messageInput');
    const content = messageInput.value;
    const messageId = "1"; // 임시로 설정한 메시지 ID, 실제 구현에서는 동적으로 설정 필요

    if (!content) {
        alert('Please enter a message.');
        return;
    }

    fetch(`${apiUrl}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `messageId=${messageId}&emoji=${encodeURIComponent(content)}`
    })
        .then(response => response.json())
        .then(reaction => {
            messageInput.value = '';
            addReactionToList(reaction);
        })
        .catch(error => {
            console.error('Error adding reaction:', error);
            alert('Failed to add reaction.');
        });
}

function loadReactions() {
    const messageId = "1"; // 임시로 설정한 메시지 ID, 실제 구현에서는 동적으로 설정 필요

    fetch(`${apiUrl}/${messageId}`, {
        method: 'GET'
    })
        .then(response => response.json())
        .then(reactions => {
            reactions.forEach(addReactionToList);
        })
        .catch(error => {
            console.error('Error loading reactions:', error);
        });
}

function addReactionToList(reaction) {
    const reactionList = document.getElementById('reactionList');

    const reactionItem = document.createElement('div');
    reactionItem.classList.add('reaction');

    const reactionContent = document.createElement('span');
    reactionContent.classList.add('reaction-content');
    reactionContent.textContent = reaction.emoji;

    const deleteButton = document.createElement('button');
    deleteButton.classList.add('delete-button');
    deleteButton.textContent = 'Delete';
    deleteButton.onclick = () => deleteReaction(reaction.id, reactionItem);

    reactionItem.appendChild(reactionContent);
    reactionItem.appendChild(deleteButton);

    reactionList.appendChild(reactionItem);
}

function deleteReaction(id, reactionItem) {
    fetch(`${apiUrl}/${id}`, {
        method: 'DELETE'
    })
        .then(() => {
            reactionItem.remove();
        })
        .catch(error => {
            console.error('Error deleting reaction:', error);
            alert('Failed to delete reaction.');
        });
}
