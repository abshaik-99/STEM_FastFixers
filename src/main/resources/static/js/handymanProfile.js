document.addEventListener("DOMContentLoaded", async () => {
  const editButton = document.getElementById("edit-button");
  const aboutInput = document.getElementById("about-input");
  const aboutText = document.getElementById("about");
  const experienceForm = document.getElementById("experience-form");
  const skillsForm = document.getElementById("skills-form");
  const uploadButton = document.getElementById("upload-button");
  const uploadInput = document.getElementById("upload-picture");
  const profilePic = document.getElementById("profile-pic");
  const nameElement = document.getElementById("name");
  const headlineElement = document.getElementById("headline");
  const locationElement = document.getElementById("location");
  const experienceList = document.getElementById("experience-list");
  const skillsList = document.getElementById("skills-list");

  // Fetch user profile data
  async function fetchProfile() {
      try {
          const response = await fetch("/api/profile"); // Adjust API endpoint
          const user = await response.json();

          // Redirect based on user role
          if (user.role === "Customer") {
            window.location.href = "/customerProfile";
            return;
            } else if (user.role === "Handyman") {
                window.location.href = "/handymanProfile";
                return;
            }

          nameElement.textContent = user.name;
          headlineElement.textContent = user.headline;
          locationElement.textContent = user.location;
          aboutText.textContent = user.about;
          profilePic.src = user.profilePic || "https://placehold.co/150";
          
          experienceList.innerHTML = user.experience.map(exp => `
              <li>
                  <h3>${exp.company} - ${exp.position}</h3>
                  <p>${exp.dates}</p>
                  <p>${exp.description}</p>
              </li>
          `).join("");

          skillsList.innerHTML = user.skills.map(skill => `<li>${skill}</li>`).join("");
      } catch (error) {
          console.error("Error fetching profile:", error);
      }
  }
  await fetchProfile();

  // Edit profile
  editButton.addEventListener("click", async () => {
      if (aboutText.style.display === "none") {
          // Save changes
          const updatedProfile = {
              about: aboutInput.value,
          };
          
          try {
              await fetch("/api/user/profile", {
                  method: "PUT",
                  headers: { "Content-Type": "application/json" },
                  body: JSON.stringify(updatedProfile)
              });
              await fetchProfile();
          } catch (error) {
              console.error("Error updating profile:", error);
          }

          aboutText.style.display = "block";
          aboutInput.style.display = "none";
          experienceForm.style.display = "none";
          skillsForm.style.display = "none";
          editButton.textContent = "Edit Profile";
      } else {
          // Enable edit mode
          aboutText.style.display = "none";
          aboutInput.style.display = "block";
          experienceForm.style.display = "block";
          skillsForm.style.display = "block";
          editButton.textContent = "Save Changes";
          aboutInput.value = aboutText.textContent;
      }
  });

  // Upload profile picture
  uploadButton.addEventListener("click", () => uploadInput.click());
  uploadInput.addEventListener("change", async (event) => {
      const file = event.target.files[0];
      if (!file || !file.type.startsWith("image/")) {
          alert("Please upload a valid image file.");
          return;
      }
      
      const formData = new FormData();
      formData.append("profilePic", file);
      
      try {
          const response = await fetch("/api/user/profile/picture", {
              method: "POST",
              body: formData
          });
          const data = await response.json();
          profilePic.src = data.profilePic;
      } catch (error) {
          console.error("Error uploading picture:", error);
      }
  });

  // Add Experience
  document.getElementById("add-experience").addEventListener("click", async () => {
      const newExperience = {
          company: document.getElementById("experience-company").value,
          position: document.getElementById("experience-position").value,
          dates: document.getElementById("experience-dates").value,
          description: document.getElementById("experience-description").value,
      };
      
      if (Object.values(newExperience).some(val => !val)) return;

      try {
          await fetch("/api/user/profile/experience", {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify(newExperience)
          });
          await fetchProfile();
      } catch (error) {
          console.error("Error adding experience:", error);
      }
  });

  // Add Skill
  document.getElementById("add-skill").addEventListener("click", async () => {
      const newSkill = document.getElementById("new-skill").value;
      if (!newSkill) return;
      
      try {
          await fetch("/api/user/profile/skills", {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({ skill: newSkill })
          });
          await fetchProfile();
      } catch (error) {
          console.error("Error adding skill:", error);
      }
  });
});