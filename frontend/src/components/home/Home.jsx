import { data, useNavigate } from "react-router-dom";
import { useAuthContext } from "../../context/AuthContext";
import NewUserModal from "../newUser/NewUserModal";
import { useState } from "react";
import { useGetAllDocuments, useGetAllUsers } from "../../hooks/useAuth";
import UserDetailsModal from "../userDetails/UserDetails";
import CreateDocument from "../createDocument/CreateDocument";
import DocDetails from "../documentDetails/DocDetails";

export default function Home() {
  const navigate = useNavigate();
  const { roleName } = useAuthContext();
  const [showModal, setShowModal] = useState(false);
  const [showCreateDoc, setShowCreateDoc] = useState(false);
  const [users, setUsers] = useGetAllUsers();
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [documents] = useGetAllDocuments();

  const [selectedDocumentId,setSelectedDocumentId] = useState(null);

  return (
    // ПОПРАВЕНО: беше calssName="home-page" (typo!) -> className="home-wrapper"
    <div className="home-wrapper">
      <header className="navbar">
        <div className="logo">
          <i className="fas fa-file-alt" /> <strong>DocVault</strong>{" "}
          <span className="doc-count">{documents.length} документа</span>
        </div>
        <div className="nav-right">
          <button className="btn-new" onClick={() => setShowCreateDoc(true)}>
            <i className="fas fa-plus" /> + Нов документ
          </button>

          {roleName === "ADMIN" && (
            <button className="btn-new" onClick={() => setShowModal(true)}>
              <i className="fas fa-plus" /> + Създай потребител
            </button>
          )}

          <div className="user-profile" onClick={() => navigate("/profile")}>
            <div className="avatar">
              <img src="/images/user.png" alt="User Avatar" />
            </div>
            <span>Иван Петров</span>
          </div>
        </div>
      </header>

      <main className="container">
        <div className="search-section">
          <i className="fas fa-search search-icon" />
          <input type="text" placeholder="Търсене на документи..." />
        </div>
        <div className="divContainer">
          <div className="content-wrapper">
            <section className="doc-scroll-area">
              <div className="doc-grid">
                {documents.length > 0 ? (
                  documents.map((document) => (
                    <div
                      key={document.id}
                      className="doc-card"
                      onClick={() => navigate(`/documents/${document.id}`)}
                    >
                      <div className="status approved">{document.versionStatus}</div>
                      <h3>{document.title}</h3>
                      <p>{document.description}</p>
                      <div className="doc-footer">
                        <span>
                          <i className="fas fa-code-branch" /> v
                          {document.version}
                        </span>
                        <span>
                          <i className="fas fa-user" />{" "}
                          {document.createdByUsername}
                        </span>
                        <span className="date">
                          {new Date(document.createdAt).toLocaleDateString(
                            "bg-BG",
                            {
                              day: "2-digit",
                              month: "2-digit",
                              year: "numeric",
                            },
                          )}
                        </span>
                      </div>
                    </div>
                  ))
                ) : (
                  <h3>No articles yet!</h3>
                )}
              </div>
            </section>

            {roleName === "ADMIN" && (
              <aside>
                <div className="active-users-panel">
                  <h4>АКТИВЕН ПОТРЕБИТЕЛ</h4>
                  <ul className="user-list">
                    {users.map((user) => (
                      <li
                        key={user.id}
                        className="user-row"
                        onClick={() => setSelectedUserId(user.id)}
                        style={{ cursor: "pointer" }}
                      >
                        <i className="fas fa-eye"></i>
                        <span className="name">{user.username}</span>
                        <span className="role">{user.role.name}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              </aside>
            )}
          </div>
        </div>
      </main>

      {selectedUserId && (
        <UserDetailsModal
          userId={selectedUserId}
          onClose={() => setSelectedUserId(null)}
          // onDeleteSuccess ={(deletedId) => {
          //   setUsers(prevUsers => prevUsers.filter(u=> u.id !== deletedId));
          // }}
        />
      )}

      {showModal && (
        <NewUserModal
          onClose={() => setShowModal(false)}
          onCreate={(newUser) => setUsers((oldUsers) => [...oldUsers, newUser])}
        />
      )}

      {showCreateDoc && (
        <CreateDocument onClose={() => setShowCreateDoc(false)} />
      )}

    </div>
  );
}
