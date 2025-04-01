document.getElementById("loginForm").addEventListener("submit", function(event) {
    event.preventDefault();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    fetch("/api/auth/login", {
        method: "POST",
        headers: { 
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, password }),
        credentials: "include"
    })
    .then(response => response.json().then(data => ({ status: response.ok, data }))) // Ensure response is parsed
    .then(({ status, data }) => {
        const loginMessage = document.getElementById("loginMessage");

        if (status) {
            // Successful login, redirect
            window.location.href = data.redirectUrl // Redirect on success
        } else {
            // Show error message and handle unregistered user
            if (loginMessage) {
                loginMessage.textContent = data.message;
                loginMessage.classList.remove("d-none");
            }
            
            // If user is not registered, show popup and redirect
            const errorMsg = data.message.toLowerCase();
            if (errorMsg.includes("Invalid credentials") || errorMsg.includes("User not found") || errorMsg.includes("email doesn't exist")) {
                if (confirm("You are not registered. Would you like to create an account?")) {
                    window.location.href = "/auth/register";
                }
            } else {
                alert(data.message);
            }
        }
    })
    .catch(error => {
        console.error("Error:", error.message);
        if (confirm("An error occurred. Would you like to register as a new user?")) {
            window.location.href = "/auth/register";
        } else {
            alert("Please try again or contact support if the problem persists.");
        }
    });
});