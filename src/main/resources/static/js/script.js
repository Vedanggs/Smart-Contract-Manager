console.log("Script loaded");

// Build headers for state-changing fetch() calls, including the CSRF token
// exposed via <meta> tags in base.html. Usage: fetch(url,{method:'POST',headers:csrfHeaders({...})})
function csrfHeaders(extra) {
  var headers = extra || {};
  var token = document.querySelector('meta[name="_csrf"]');
  var header = document.querySelector('meta[name="_csrf_header"]');
  if (token && header && header.content) {
    headers[header.content] = token.content;
  }
  return headers;
}

// change theme work
let currentTheme = getTheme();
//initial -->

document.addEventListener("DOMContentLoaded", () => {
  changeTheme();
});

//TODO:
function changeTheme() {
  //set to web page

  changePageTheme(currentTheme, "");
  //set the listener to change theme button
  const changeThemeButton = document.querySelector("#theme_change_button");

  changeThemeButton.addEventListener("click", (event) => {
    let oldTheme = currentTheme;
    console.log("change theme button clicked");
    if (currentTheme === "dark") {
      //theme ko light
      currentTheme = "light";
    } else {
      //theme ko dark
      currentTheme = "dark";
    }
    console.log(currentTheme);
    changePageTheme(currentTheme, oldTheme);
  });
}

//set theme to localstorage
function setTheme(theme) {
  localStorage.setItem("theme", theme);
}

//get theme from localstorage
function getTheme() {
  let theme = localStorage.getItem("theme");
  return theme ? theme : "light";
}

//change current page theme
function changePageTheme(theme, oldTheme) {
  //localstorage mein update karenge
  setTheme(currentTheme);
  //remove the current theme

  if (oldTheme) {
    document.querySelector("html").classList.remove(oldTheme);
  }
  //set the current theme
  document.querySelector("html").classList.add(theme);

  // change the text of button
  document
    .querySelector("#theme_change_button")
    .querySelector("span").textContent = theme == "light" ? "Dark" : "Light";
}

//change page change theme
