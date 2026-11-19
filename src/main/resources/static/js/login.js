document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("loginForm");
    const customerInput = document.getElementById("customerId");
    const passwordInput = document.getElementById("password");

    if (!form || !customerInput || !passwordInput) {
        console.error("Login form elements are missing.");
        return;
    }

    // Create a message area for login results.
    const status = document.createElement("p");
    status.className = "login-status";
    status.setAttribute("role", "status");
    status.setAttribute("aria-live", "polite");
    form.insertAdjacentElement("afterend", status);

    // Remove the old preview-only message.
    document.querySelector(".preview-note")?.remove();

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const button = form.querySelector(".login-button");
        const originalButtonContent = button?.innerHTML;

        const customerNumber = customerInput.value.trim();
        const password = passwordInput.value;

        if (!customerNumber || !password) {
            status.textContent = "Enter your customer number and password.";
            status.dataset.type = "error";
            return;
        }

        if (button) {
            button.disabled = true;
            button.textContent = "Signing in...";
        }

        status.textContent = "";
        status.dataset.type = "";

        try {
            // First obtain a CSRF token.
            const csrfResponse = await fetch("/api/auth/csrf", {
                method: "GET",
                credentials: "same-origin"
            });

            if (!csrfResponse.ok) {
                throw new Error("Unable to initialize secure login. Please try again.");
            }

            const csrfData = await csrfResponse.json();

            // Authenticate using the existing backend endpoint.
            const loginResponse = await fetch("/api/auth/login", {
                method: "POST",
                credentials: "same-origin",
                headers: {
                    "Content-Type": "application/json",
                    "X-XSRF-TOKEN": csrfData.token
                },
                body: JSON.stringify({
                    customerNumber: customerNumber,
                    password: password
                })
            });

            const loginData = await loginResponse.json().catch(() => ({}));

            if (!loginResponse.ok) {
                throw new Error(
                    loginData.message ||
                    loginData.error ||
                    "Login failed. Check your customer number and password."
                );
            }

            // Verify the authenticated session.
            const meResponse = await fetch("/api/auth/me", {
                method: "GET",
                credentials: "same-origin"
            });

            if (!meResponse.ok) {
                throw new Error("Login response received, but session verification failed.");
            }

            const user = await meResponse.json();

            passwordInput.value = "";
            window.location.href = "/dashboard.html";

        } catch (error) {
            status.dataset.type = "error";
            status.textContent = error.message || "Something went wrong. Please try again.";
        } finally {
            if (button) {
                button.disabled = false;
                button.innerHTML = originalButtonContent;
            }
        }
    });
});
