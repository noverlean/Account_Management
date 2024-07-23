var authPage = `
    <div id="auth">
        <p>Авторизация</p>
        <input id="nickname" type="text" autocomplete="off">
        <input id="password" type="password" autocomplete="off">
        <input type="button" value="send" onclick="LogIn()">
        <p class="acc-ex" onclick="SwitchAuthAndReg(true)">Еще нет аккаунт, зарегистрироваться!</p>
    </div>
`;

var regPage = `
    <div id="reg">
        <p class="label">Регистрация</p>
        <input id="fullname" type="text" autocomplete="off" placeholder="full name">
        <input id="nickname" type="text" autocomplete="off"  placeholder="nickname">
        <input id="password" type="password" autocomplete="off"  placeholder="password">
        <input type="button" value="send" onclick="Reg()">
        <p class="acc-ex" onclick="SwitchAuthAndReg(false)">Уже есть аккаунт. Войти!</p>
    </div>
`;
var jwt = "";

SwitchAuthAndReg(true);
CheckJWT();

console.log(localStorage.getItem("jwt"));

function CheckJWT()
{
    if (localStorage.getItem("jwt") == null || localStorage.getItem("nickname") == null)
    {
        if (window.location.href != "http://localhost:8080/auth.html")
        {
            window.location.href = "http://localhost:8080/auth.html";
        }
    }
    else
    {
        if (window.location.href != "http://localhost:8080/index.html")
        {
            window.location.href = "http://localhost:8080/index.html";
        }
        else
        {
            LoadAccount();
        }
    }
}

function LoadAccount()
{
    $.ajax({
        url: `http://localhost:8080/users/fetch`,
        method: 'get',
        headers: {
            'Authorization':`Bearer ${localStorage.getItem("jwt")}`,
            'Content-Type':'application/json'
        },
        success: function(data){
            console.log(data);
            FillUserProfile(data);
        },
        error: function (jqXHR, exception) {
            console.log(jqXHR.responseText);
            if (jqXHR.status === 0) {
                alert('Not connect. Verify Network.');
            } else if (jqXHR.status == 401) {
                alert('Такого пользователя не существует (401).');
            } else if (jqXHR.status == 404) {
                alert('Requested page not found (404).');
            } else if (jqXHR.status == 500) {
                alert('Internal Server Error (500).');
            } else if (exception === 'parsererror') {
                alert('Requested JSON parse failed.');
            } else if (exception === 'timeout') {
                alert('Time out error.');
            } else if (exception === 'abort') {
                alert('Ajax request aborted.');
            } else {

                $.ajax({
                    url: `http://localhost:8080/users/accounts`,
                    method: 'get',
                    headers: {
                        'Authorization':`Bearer ${localStorage.getItem("jwt")}`,
                        'Content-Type':'application/json'
                    },
                    success: function(data){
                        console.log(data);
                        FillAdminPage(data);
                    },
                    error: function (jqXHR, exception) {
                        console.log(jqXHR.responseText);
                        if (jqXHR.status === 0) {
                            alert('Not connect. Verify Network.');
                        } else if (jqXHR.status == 401) {
                            alert('Такого пользователя не существует (401).');
                        } else if (jqXHR.status == 404) {
                            alert('Requested page not found (404).');
                        } else if (jqXHR.status == 500) {
                            alert('Internal Server Error (500).');
                        } else if (exception === 'parsererror') {
                            alert('Requested JSON parse failed.');
                        } else if (exception === 'timeout') {
                            alert('Time out error.');
                        } else if (exception === 'abort') {
                            alert('Ajax request aborted.');
                        } else {
                            alert('Uncaught Error. ' + jqXHR.responseText);
                        }
                        }
                });

            }
            }
    });
}

function FillUserProfile(data)
{
    document.getElementById('main').innerHTML = "";
    document.getElementById('header').innerHTML = `<br><h2 style="margin-left: 75px">${data.nickname}</h2><h4 style="margin-left: 75px">${data.fullname}</h4><br>`;
    data.accounts.forEach(account => {
        document.getElementById('main').innerHTML += `<div id="${account.id}" class="account">
            <p>Номер счета: ${account.id}</p>
            <p>Баланс: ${account.amount}</p>
            <p>Состояние: ${account.isBlocked ? "Заблокирован" : "Доступен"}</p>
        </div>`;
    });
    
}

function FillAdminPage(data)
{
    document.getElementById('tab').innerHTML = "";
    document.getElementById('main').innerHTML = "";
    document.getElementById('header').innerHTML = `<br><h1 style="margin-left: 50px">ADMIN</h1><br>`;
    data.forEach(account => {
        document.getElementById('main').innerHTML += `<div class="account">
            <p>Номер счета: ${account.id}</p>
            <p>Баланс: ${account.amount}</p>
            <p>Состояние: ${account.isBlocked ? "Заблокирован" : "Доступен"}</p>
            <button onclick='BlockAccount(${account.id}, ${!account.isBlocked})'>${account.isBlocked ? "Разблокировать" : "Заблокировать"} </div> 
        </div>`;
    });
}

function Reg()
{
    let data =
    {
        fullname: document.getElementById('fullname').value,
        nickname: document.getElementById('nickname').value,
        password: document.getElementById('password').value,
    };
    console.log(JSON.stringify(data));
    
    $.ajax({
        url: 'http://localhost:8080/users/register',
        method: 'post',
        headers: {
            // 'Authorization':'Basic xxxxxxxxxxxxx',
            'Content-Type':'application/json'
        },
        data: JSON.stringify(data),
        success: function(data){
            console.log(data);
            alert("Success!");
            SwitchAuthAndReg(false);
        },
        error: function (jqXHR, exception) {
            console.log(jqXHR.responseText);
            if (jqXHR.status === 0) {
                alert('Not connect. Verify Network.');
            } else if (jqXHR.status == 401) {
                alert('Такого пользователя не существует (401).');
            } else if (jqXHR.status == 404) {
                alert('Requested page not found (404).');
            } else if (jqXHR.status == 500) {
                alert('Internal Server Error (500).');
            } else if (exception === 'parsererror') {
                alert('Requested JSON parse failed.');
            } else if (exception === 'timeout') {
                alert('Time out error.');
            } else if (exception === 'abort') {
                alert('Ajax request aborted.');
            } else {
                alert('Uncaught Error. ' + jqXHR.responseText);
            }
            }
    });
}

function LogIn()
{
    let data =
    {
        nickname: document.getElementById('nickname').value,
        password: document.getElementById('password').value,
    };
    console.log(JSON.stringify(data));
    
    $.ajax({
        url: 'http://localhost:8080/users/login',
        method: 'post',
        headers: {
            // 'Authorization':'Basic xxxxxxxxxxxxx',
            'Content-Type':'application/json'
        },
        data: JSON.stringify(data),
        success: function(_data){
            console.log(_data);
            
            localStorage.setItem("jwt", _data.jwt);
            localStorage.setItem("nickname", data.nickname)
            console.log(localStorage.getItem("jwt"));
            console.log(localStorage.getItem("nickname"));
            window.location.href = "http://localhost:8080/index.html";
        },
        error: function (jqXHR, exception) {
            if (jqXHR.status === 0) {
                alert('Not connect. Verify Network.');
            } else if (jqXHR.status == 401) {
                alert('Такого пользователя не существует (401).');
            } else if (jqXHR.status == 404) {
                alert('Requested page not found (404).');
            } else if (jqXHR.status == 500) {
                alert('Internal Server Error (500).');
            } else if (exception === 'parsererror') {
                alert('Requested JSON parse failed.');
            } else if (exception === 'timeout') {
                alert('Time out error.');
            } else if (exception === 'abort') {
                alert('Ajax request aborted.');
            } else {
                alert('Uncaught Error. ' + jqXHR.responseText);
            }
            }
    });
}

function SwitchAuthAndReg(state)
{
    if (window.location.href != "http://localhost:8080/index.html")
        document.getElementById("main").innerHTML = !state ? authPage : regPage;
}

function add()
{
    let data =
    {
        changeValue: document.getElementById('value').value,
        accountId: document.getElementById('num').value,
    };

    $.ajax({
        url: `http://localhost:8080/users/accounts/${data.accountId}/add/${data.changeValue}`,
        method: 'post',
        headers: {
            'Authorization':`Bearer ${localStorage.getItem("jwt")}`,
            'Content-Type':'application/json'
        },
        success: function(data){
            console.log(data);
            FillUserProfile(data);
        },
        error: function (jqXHR, exception) {
            console.log(jqXHR.responseText);
            if (jqXHR.status === 0) {
                alert('Not connect. Verify Network.');
            } else if (jqXHR.status == 401) {
                alert('Такого пользователя не существует (401).');
            } else if (jqXHR.status == 404) {
                alert('Requested page not found (404).');
            } else if (jqXHR.status == 500) {
                alert('Internal Server Error (500).');
            } else if (exception === 'parsererror') {
                alert('Requested JSON parse failed.');
            } else if (exception === 'timeout') {
                alert('Time out error.');
            } else if (exception === 'abort') {
                alert('Ajax request aborted.');
            } else {
                alert('Uncaught Error. ' + jqXHR.responseText);
            }
            }
    });
}

function withdraw()
{
    let data =
    {
        changeValue: document.getElementById('value').value,
        accountId: document.getElementById('num').value,
    };

    $.ajax({
        url: `http://localhost:8080/users/accounts/${data.accountId}/withdraw/${data.changeValue}`,
        method: 'post',
        headers: {
            'Authorization':`Bearer ${localStorage.getItem("jwt")}`,
            'Content-Type':'application/json'
        },
        success: function(data){
            console.log(data);
            FillUserProfile(data);
        },
        error: function (jqXHR, exception) {
            console.log(jqXHR.responseText);
            if (jqXHR.status === 0) {
                alert('Not connect. Verify Network.');
            } else if (jqXHR.status == 401) {
                alert('Такого пользователя не существует (401).');
            } else if (jqXHR.status == 404) {
                alert('Requested page not found (404).');
            } else if (jqXHR.status == 500) {
                alert('Internal Server Error (500).');
            } else if (exception === 'parsererror') {
                alert('Requested JSON parse failed.');
            } else if (exception === 'timeout') {
                alert('Time out error.');
            } else if (exception === 'abort') {
                alert('Ajax request aborted.');
            } else {
                alert('Uncaught Error. ' + jqXHR.responseText);
            }
            }
    });
}

function BlockAccount(accountId, newState)
{
    $.ajax({
        url: `http://localhost:8080/users/accounts/${accountId}/${newState ? "" : "un"}block`,
        method: 'post',
        headers: {
            'Authorization':`Bearer ${localStorage.getItem("jwt")}`,
            'Content-Type':'application/json'
        },
        success: function(data){
            console.log(data);
            FillAdminPage(data);
        },
        error: function (jqXHR, exception) {
            console.log(jqXHR.responseText);
            if (jqXHR.status === 0) {
                alert('Not connect. Verify Network.');
            } else if (jqXHR.status == 401) {
                alert('Такого пользователя не существует (401).');
            } else if (jqXHR.status == 404) {
                alert('Requested page not found (404).');
            } else if (jqXHR.status == 500) {
                alert('Internal Server Error (500).');
            } else if (exception === 'parsererror') {
                alert('Requested JSON parse failed.');
            } else if (exception === 'timeout') {
                alert('Time out error.');
            } else if (exception === 'abort') {
                alert('Ajax request aborted.');
            } else {
                alert('Uncaught Error. ' + jqXHR.responseText);
            }
            }
    });
}

function Exit()
{
    localStorage.clear();
    window.location.href = "http://localhost:8080/auth.html"
}