var authPage = `
    <div id="auth">
        <p>Авторизация</p>
        <input type="text" autocomplete="off">
        <input type="password" autocomplete="off">
        <input type="button" value="send">
        <p class="acc-ex" onclick="SwitchAuthAndReg(true)">Уже есть аккаунт. Войти!</p>
    </div>
`;

var regPage = `
    <div id="reg">
        <p>Регистрация</p>
        <input type="text" autocomplete="off">
        <input type="password" autocomplete="off">
        <input type="button" value="send">
        <p class="acc-ex" onclick="SwitchAuthAndReg(false)">Еще нет аккаунт, зарегистрироваться!</p>
    </div>
`;
var jwt = "";
// SwitchAuthAndReg(true);
function CheckJWT()
{
    jwt = localStorage.getItem("jwt");
}

function Reg()
{
    let data =
    {
        fulname: document.getElementById('fullname').value,
        nickname: document.getElementById('nickname').value,
        password: document.getElementById('password').value,
    };
    
    $.ajax({
        url: 'http://localhost:8080/users/register',
        method: 'post',
        dataType: 'application/json',
        data: data,
        success: function(data){
            alert(data);
        }
    });
}

function LogIn()
{
    let data =
    {
        fulname: document.getElementById('fullname').value,
        nickname: document.getElementById('nickname').value,
        password: document.getElementById('password').value,
    };
    
    $.ajax({
        url: 'http://localhost:8080/users/register',
        method: 'post',
        dataType: 'application/json',
        data: data,
        success: function(data){
            alert(data);
        }
    });
}

function SwitchAuthAndReg(state)
{
    document.getElementById("main").innerHTML = !state ? authPage : regPage;
}