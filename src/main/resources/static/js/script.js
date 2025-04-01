document.addEventListener("DOMContentLoaded", function () {
    // AOS Animation Initialization
    AOS.init();

    // Fetch Jobs on Page Load
    // fetchJobs();

    // Initialize Counters with Animation
    let counters = document.querySelectorAll(".counter");

    function animateCounter(counter) {
        console.log(`Animating counter: ${counter.getAttribute("data-count")}`); // Debugging
        let target = parseInt(counter.getAttribute("data-count"));
        let current = 0;
        let step = Math.ceil(target / 100);

        let interval = setInterval(() => {
            current += step;
            if (current >= target) {
                counter.textContent = target;
                clearInterval(interval);
            } else {
                counter.textContent = current;
            }
        }, 20); // Adjust timing for smooth effect
    }

    let observer = new IntersectionObserver(entries => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                animateCounter(entry.target);
            }
        });
    }, { threshold: 0.5 });

    counters.forEach(counter => observer.observe(counter));

    const postJobLink = document.getElementById("postJobLink");
    if (postJobLink) {
        postJobLink.addEventListener("click", function(event) {
            event.preventDefault();
            fetch("/api/check-session1")
                .then(response => response.json())
                .then(data => {
                    if (data.loggedIn) {
                        window.location.href = "/api/job/postjob";
                    } else {
                        alert("You need to register or login first.");
                        window.location.href = "/auth/register";
                    }
                })
                .catch(error => console.error("Error:", error));
        });
    }

    const jobSearchForm = document.getElementById("jobSearchForm");
    if (jobSearchForm) {
        jobSearchForm.addEventListener("submit", function (event) {
            event.preventDefault();
            fetchJobs();
        });
    }
});

// Fetch Jobs
function fetchJobs() {
    const title = document.getElementById("jobTitleInput")?.value.trim() || "";
    const location = document.getElementById("locationInput")?.value.trim() || "";
    const jobList = document.getElementById("jobList");

    jobList.innerHTML = '<p class="text-center text-muted">Loading jobs...</p>';

    // Construct the query parameters
    let apiUrl = `/api/job/search?`;
    if (title) apiUrl += `title=${encodeURIComponent(title)}&`;
    if (location) apiUrl += `location=${encodeURIComponent(location)}`;

    fetch(apiUrl)
        .then(response => {
            if (!response.ok) throw new Error("Failed to fetch jobs");
            return response.json();
        })
        .then(data => {
            jobList.innerHTML = ''; // Clear previous results

            if (data.length === 0) {
                jobList.innerHTML = '<p class="text-center text-danger">No jobs found.</p>';
            } else {
                data.forEach(job => {
                    const jobCard = document.createElement('div');
                    jobCard.classList.add('col-md-4', 'job-card');
                    jobCard.dataset.jobId = job.id;
                    jobCard.innerHTML = `
                        <div class="card shadow-sm border-0">
                            <div class="card-body">
                                <h5 class="card-title text-primary">${job.title}</h5>
                                <h6 class="card-subtitle mb-2 text-muted"><i class="bi bi-geo-alt"></i> ${job.location}</h6>
                                <p class="card-text">${job.description}</p>
                                <p class="card-text"><strong>Budget:</strong> $${job.budget}</p>
                            </div>
                        </div>
                    `;
                    jobList.appendChild(jobCard);
                });

                // Add click event to job cards to check session before redirecting
                document.querySelectorAll('.job-card').forEach(card => {
                    card.addEventListener('click', function () {
                        const jobId = this.dataset.jobId;
                        fetch("/api/check-session1")
                            .then(response => response.json())
                            .then(data => {
                                if (data.loggedIn) {
                                    window.location.href = `/api/job/description/${jobId}`;
                                } else {
                                    alert("You need to register or login first.");
                                    window.location.href = "/auth/login";
                                }
                            })
                            .catch(error => console.error("Error:", error));
                    });
                });
            }
        })
        .catch(error => {
            jobList.innerHTML = `<p class="text-center text-danger">Error fetching jobs: ${error.message}</p>`;
        });
}

// Post Job Form Submission (jQuery)
$(document).ready(function () {
    $('#postJobForm').submit(function (event) {
        event.preventDefault();

        var jobTitle = $('#jobTitle').val().trim();
        var jobDescription = $('#jobDescription').val().trim();
        var location = $('#location').val().trim();
        var budget = $('#budget').val().trim();

        if (!jobTitle || !jobDescription || !location || !budget) {
            alert('Please fill out all fields.');
            return;
        }

        $.ajax({
            type: 'POST',
            url: '/api/jobs',
            data: JSON.stringify({
                title: jobTitle,
                description: jobDescription,
                location: location,
                budget: budget
            }),
            contentType: 'application/json',
            success: function (response) {
                alert('Job posted successfully!');
                window.location.href = '/job-posted';
            },
            error: function (error) {
                alert('Error posting job: ' + error.responseText);
            }
        });
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const contactForm = document.getElementById('contactForm');

    if (!contactForm) {
        console.warn('⚠️ contactForm not found. Skipping form submission setup.');
        return;
    }

    contactForm.addEventListener("submit", function (e) {
        e.preventDefault();

        let formData = {
            name: document.getElementById("name").value.trim(),
            email: document.getElementById("email").value.trim(),
            phone: document.getElementById("phone").value.trim(),  
            subject: document.getElementById("subject").value.trim(),
            message: document.getElementById("message").value.trim()
        };

        fetch("http://localhost:8080/api/email/send", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(formData)
        })
        .then(response => {
            if (!response.ok) throw new Error(`Server error: ${response.status}`);
            return response.text();
        })
        .then(data => {
            alert("✅ Email Sent Successfully!");
            contactForm.reset();
        })
        .catch(error => {
            console.error("❌ Fetch Error:", error);
            alert("❌ Failed to send email. Check console.");
        });
    });
});
