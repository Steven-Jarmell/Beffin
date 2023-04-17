import { useEffect, useState } from "react";
import "./CSS/LoginPage.css";
import { Navigate, useNavigate } from "react-router-dom";

const LoginPage = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const navigate = useNavigate();

    const logInfo = (e) => {
        e.preventDefault();

        const requestOptions = {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                email: email,
                password: password,
            }),
        };
        let token = "";
        //Use /register route
        //Send post of user reques
        fetch("http://localhost:8080/api/auth/authenticate", requestOptions)
            .then((response) => response.json())
            .then((result) => {
                token = result.token;
                sessionStorage.setItem("token", token);
                if (token) navigate("/profile");
            })
            .catch((error) => navigate("/login"));

        setEmail("");
        setPassword("");
    };

    return (
        <div className="Login">
            <p className="loginLabel">Login</p>
            <form onSubmit={(e) => logInfo(e)} className="login-form-container">
                <label htmlFor="email">Email: </label>
                <input
                    type="text"
                    id="email"
                    name="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="Enter Email Here"
                />
                <label htmlFor="password">Password: </label>
                <input
                    type="text"
                    id="password"
                    name="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Enter Password Here"
                />
                <button className="submitButton" onClick={(e) => logInfo(e)}>
                    Submit
                </button>
            </form>
        </div>
    );
};

export default LoginPage;
