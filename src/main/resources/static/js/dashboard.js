function toggleCVV() {
    const cvv = document.getElementById("cvv-code");
    if (cvv.style.display === "none") {
        cvv.style.display = "block";  // Показываем CVV
    } else {
        cvv.style.display = "none";   // Скрываем CVV
    }
}
