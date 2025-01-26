function formatCardNumber() {
    const cardNumberInput = document.getElementById('card-number');
    let value = cardNumberInput.value.replace(/\D/g, ''); // Убираем все нецифровые символы

    if (value.length > 16) {
        value = value.slice(0, 16); // Ограничиваем 16 символами
    }

    // Форматируем номер карты с пробелами через каждые 4 цифры
    let formattedValue = '';
    for (let i = 0; i < value.length; i++) {
        if (i > 0 && i % 4 === 0) {
            formattedValue += ' ';
        }
        formattedValue += value[i];
    }

    cardNumberInput.value = formattedValue; // Обновляем поле ввода с отформатированным значением
}
function checkInput(input) {
    let value = input.value;
    // Убираем все, что после второй цифры после точки
    if (value.includes(".")) {
        let parts = value.split(".");
        if (parts[1].length > 2) {
            parts[1] = parts[1].slice(0, 2); // Ограничиваем двумя знаками
            input.value = parts.join(".");
        }
    }
    // Проверка на отрицательные значения
    if (parseFloat(input.value) < 0) {
        input.value = 0; // Заменяем на 0, если число отрицательное
        alert("Xahiş edirik, mənfi ədədlər daxil etməyin!");
    }
}
