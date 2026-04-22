import { useState } from "react";
import { useLogin } from "../../hooks/useAuth";
import { useNavigate } from "react-router-dom";
import { useForm } from "../../hooks/useForm";
const initialValues = { email: "", password: "" };

export default function Login() {
  const login = useLogin();
  const navigate = useNavigate();
  const [error, setError] = useState(null);

  const loginHendler = async ({ email, password }) => {
    try {
      await login(email, password);
      navigate("/");
    } catch (error) {
      setError(error.message);
    }
  };

  const { values, changeHendler, submitHendler } = useForm(
    initialValues,
    loginHendler,
  );

  return (
    <div className="loginPage">
      {/* ПОПРАВЕНО: беше className="container" -> "login-container" */}
      <div className="login-container">
        <div className="logo">
          <img src="/images/logo.png" alt="logo" />
        </div>
        <h1>Sign in to LogicLab</h1>
        <div className="login-box">
          <form action="">
            <label>Username or email address</label>
            <input
              name="email"
              type="email"
              value={values.email}
              onChange={changeHendler}
              required
              placeholder="Enter email"
            />
            <label>Password</label>

            <input
              name="password"
              type="password"
              value={values.password}
              onChange={changeHendler}
              required
              autoComplete="current-password"
              placeholder="Password"
            />
            {error && (
              <div style={{ fontSize: "20px", color: "red" }}>{error}</div>
            )}
            {/* ПОПРАВЕНО: form + button -> само button с onClick */}
            <button className="sign-in" onClick={submitHendler}>
              Sign in
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
