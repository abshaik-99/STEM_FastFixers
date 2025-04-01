document.getElementById("registerForm").addEventListener("submit", function(event) {
    event.preventDefault();

    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const phoneNumber = document.getElementById("phoneNumber").value.trim();
    const role = document.getElementById("role").value;

    const registerMessage = document.getElementById("registerMessage"); // Error message div
    if (registerMessage) {
        registerMessage.classList.add("d-none"); // Hide previous messages
        registerMessage.textContent = ""; // Clear previous messages
    }

    const formData = {
        name: name,
        email: email,
        password: password,
        phoneNumber: phoneNumber,
        role: role
    };

    fetch("/api/auth/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(formData)
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(errorMessage => { // Read error as plain text
                throw new Error(errorMessage);
            });
        }
        return response.text(); // Read success message
    })
    .then(data => {
        alert("Registration successful!");
        window.location.href = "/auth/login"; // Redirect to login page
    })
    .catch(error => {
        console.error("Error:", error);
        if (registerMessage) {
            registerMessage.textContent = error.message; // Show backend error message
            registerMessage.classList.remove("d-none"); // Show the message div
        }
    
        // Highlight specific fields based on error message
        if (error.message.includes("Email already registered")) {
            document.getElementById("email").classList.add("is-invalid");
            document.getElementById("loginOption")?.classList.remove("d-none"); // Show login option
        }
        if (error.message.includes("Phone number already registered")) {
            document.getElementById("phoneNumber").classList.add("is-invalid");
        }
        if (error.message.includes("Password must be at least")) {
            document.getElementById("password").classList.add("is-invalid");
        }
    });    
});
