// import { useState } from "react";
// import { useLogin } from "../../hooks/useAuth";
// import { useNavigate } from "react-router-dom";
// import { useForm } from "../../hooks/useForm";

// const initialValues = { email: "", password: "" };

// export default function Login() {
//   const login = useLogin();
//   const navigate = useNavigate();
//   const [error, setError] = useState(null);

//   const loginHendler = async ({ email, password }) => {
//     try {
//       await login(email, password);
//       navigate("/");
//     } catch (error) {
//       setError(error.message);
//     }
//   };

//   const { values, changeHendler, submitHendler } = useForm(
//     initialValues,
//     loginHendler,
//   );

//   return (
//     <div className="loginPage">
//       <div className="container">
//         <div className="logo">
//           <img src="https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png" />
//         </div>
//         <h1>Sign in to GitHub</h1>
//         <div className="login-box">
//           <label>Username or email address</label>

//           <input
//             name="email"
//             type="email"
//             value={values.email}
//             onChange={changeHendler}
//             required
//             placeholder="Enter email"
//           />

//           <input
//             name="password"
//             type="password"
//             value={values.password}
//             onChange={changeHendler}
//             required
//             autoComplete="current-password"
//             placeholder="Password"
//           />
//           {error && (
//             <div style={{ fontSize: "20px", color: "red" }}>{error}</div>
//           )}
//           <form action="" onClick={submitHendler}>
//             <button  className="sign-in">
//               Sign in
//             </button>
//           </form>
//         </div>
//         <p className="signup">
//           New to GitHub? <a href="./register.html">Create an account</a>
//         </p>
//       </div>
//     </div>
//   );
// }


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
    loginHendler
  );

  return (
    <div className="loginPage">
      {/* ПОПРАВЕНО: беше className="container" -> "login-container" */}
      <div className="login-container">
        <div className="logo">
          <img src="https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png" alt="logo" />
        </div>
        <h1>Sign in to GitHub</h1>
        <div className="login-box">
          <label>Username or email address</label>
          <input
            name="email"
            type="email"
            value={values.email}
            onChange={changeHendler}
            required
            placeholder="Enter email"
          />
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
        </div>
        <p className="signup">
          New to GitHub? <a href="./register.html">Create an account</a>
        </p>
      </div>
    </div>
  );
}
