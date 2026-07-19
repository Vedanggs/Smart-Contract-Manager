console.log("Contacts.js");
// Use a relative base URL so the app works on any host (localhost, staging, prod)
const baseURL = "";
const viewContactModal = document.getElementById("view_contact_modal");

// options with default values
const options = {
  placement: "bottom-right",
  backdrop: "dynamic",
  backdropClasses: "bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40",
  closable: true,
  onHide: () => {
    console.log("modal is hidden");
  },
  onShow: () => {
    setTimeout(() => {
      contactModal.classList.add("scale-100");
    }, 50);
  },
  onToggle: () => {
    console.log("modal has been toggled");
  },
};

// instance options object
const instanceOptions = {
  id: "view_contact_mdoal",
  override: true,
};

const contactModal = new Modal(viewContactModal, options, instanceOptions);

function openContactModal() {
  contactModal.show();
}

function closeContactModal() {
  contactModal.hide();
}

async function loadContactdata(id) {
  //function call to load data
  console.log(id);
  try {
    const data = await (await fetch(`${baseURL}/api/contacts/${id}`)).json();
    console.log(data);
    document.querySelector("#contact_name").innerHTML = data.name;
    document.querySelector("#contact_email").innerHTML = data.email;
    document.querySelector("#contact_image").src = data.picture;
    document.querySelector("#contact_address").innerHTML = data.address;
    document.querySelector("#contact_phone").innerHTML = data.phoneNumber;
    document.querySelector("#contact_about").innerHTML = data.description;
    const contactFavorite = document.querySelector("#contact_favorite");
    if (data.favorite) {
      contactFavorite.innerHTML =
        "<i class='fas fa-star text-yellow-400'></i><i class='fas fa-star text-yellow-400'></i><i class='fas fa-star text-yellow-400'></i><i class='fas fa-star text-yellow-400'></i><i class='fas fa-star text-yellow-400'></i>";
    } else {
      contactFavorite.innerHTML = "Not Favorite Contact";
    }

    document.querySelector("#contact_website").href = data.websiteLink;
    document.querySelector("#contact_website").innerHTML = data.websiteLink;
    document.querySelector("#contact_linkedIn").href = data.linkedInLink;
    document.querySelector("#contact_linkedIn").innerHTML = data.linkedInLink;
    openContactModal();
  } catch (error) {
    console.log("Error: ", error);
  }
}

// delete contact

async function deleteContact(id) {
  Swal.fire({
    title: "Delete this contact?",
    text: "This action cannot be undone.",
    icon: "warning",
    showCancelButton: true,
    reverseButtons: true,
    focusCancel: true,
    confirmButtonText: "Yes, delete it",
    cancelButtonText: "No, keep it",
    confirmButtonColor: "#dc2626",
    cancelButtonColor: "#6b7280",
  }).then((result) => {
    if (result.isConfirmed) {
      const url = `${baseURL}/user/contacts/delete/` + id;
      window.location.replace(url);
    }
  });
}

// Toggle a contact's favorite status without leaving the page
async function toggleFavorite(id, btn) {
  try {
    const res = await fetch(`${baseURL}/user/contacts/favorite/${id}`, {
      method: "POST",
      headers: csrfHeaders({ "X-Requested-With": "XMLHttpRequest" }),
    });
    if (!res.ok) throw new Error("Request failed");
    const data = await res.json();
    const icon = btn.querySelector("i");
    if (data.favorite) {
      icon.classList.remove("fa-regular", "text-gray-400");
      icon.classList.add("fa-solid", "text-yellow-400");
      btn.title = "Remove from favorites";
    } else {
      icon.classList.remove("fa-solid", "text-yellow-400");
      icon.classList.add("fa-regular", "text-gray-400");
      btn.title = "Add to favorites";
    }
  } catch (e) {
    Swal.fire({
      icon: "error",
      title: "Could not update favorite",
      text: "Please try again.",
    });
  }
}
