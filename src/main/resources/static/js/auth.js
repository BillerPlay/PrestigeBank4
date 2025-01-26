// Function to handle login
function login() {
    const email = document.getElementById('eMail').value;
    const password = document.getElementById('password').value;

    fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email: email, password: password })
    })
        .then(response => {
            console.log('Response:', response);  // Логируем ответ сервера
            if (response.ok) {
                return response.json(); // Предполагаем, что ответ содержит токен
            }
            throw new Error('Login failed');
        })
        .then(data => {
            console.log('Login successful, token:', data.token);
            localStorage.setItem('Authorization', data.token);
            loadDashboard();
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Login failed. Please check your credentials.');
        });
}

// Attach the login function to the form submission
document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent the default form submission
    login(); // Call the login function
});

// Function to load the dashboard
function loadDashboard() {
    const token = localStorage.getItem('Authorization');
    if (!token) {
        alert('No token found. Please log in first.');
        window.location.href = '/login'; // Redirect to login if no token is found
        return;
    }
    if (token) {
        fetch('/dashboard', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token  // Токен отправляется в заголовке
            }
        })
            .then(response => response.json())
            .then(data => console.log('Response:', data))
            .catch(error => console.error('Error:', error));
    } else {
        console.error('JWT Token is missing');

    }
}
// Call loadDashboard when the page loads, if the user is already logged in
if (window.location.pathname === '/dashboard') {
    loadDashboard();
}
