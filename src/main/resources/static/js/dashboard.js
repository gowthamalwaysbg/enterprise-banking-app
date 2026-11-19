document.addEventListener("DOMContentLoaded", async () => {
    const status = document.getElementById("dashboardStatus");
    const logoutButton = document.getElementById("logoutButton");

    try {
        const response = await fetch("/api/customers/me", {
            credentials: "same-origin"
        });

        if (response.status === 401 || response.status === 403) {
            window.location.replace("/");
            return;
        }

        if (!response.ok) {
            throw new Error("Unable to load customer profile.");
        }

        const customer = await response.json();

        document.getElementById("customerName").textContent =
            customer.firstName + " " + customer.lastName;

        document.getElementById("customerNumber").textContent =
            customer.customerNumber;

        document.getElementById("profileCustomerId").textContent =
            customer.customerNumber;

        document.getElementById("profileEmail").textContent =
            customer.email;

        document.getElementById("profileMobile").textContent =
            customer.mobileNumber;

        document.getElementById("profileStatus").textContent =
            customer.status;

    } catch (error) {
        status.textContent = error.message;
        status.dataset.type = "error";
    }

    logoutButton.addEventListener("click", async () => {
        logoutButton.disabled = true;

        try {
            const csrfResponse = await fetch("/api/auth/csrf", {
                credentials: "same-origin"
            });

            if (!csrfResponse.ok) {
                throw new Error("Unable to initialize logout.");
            }

            const csrf = await csrfResponse.json();

            const logoutResponse = await fetch("/api/auth/logout", {
                method: "POST",
                credentials: "same-origin",
                headers: {
                    "X-XSRF-TOKEN": csrf.token
                }
            });

            if (!logoutResponse.ok) {
                throw new Error("Logout failed. Please try again.");
            }

            window.location.replace("/");
        } catch (error) {
            status.textContent = error.message;
            status.dataset.type = "error";
            logoutButton.disabled = false;
        }
    });
});
