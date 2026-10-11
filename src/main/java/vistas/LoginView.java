package vistas;

import datos.UsuarioDAO;
import models.Usuario;

import javax.swing.*;

public class LoginView extends JFrame {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private JTextField txtCorreo;
    private JPasswordField txtPassword;
    private JButton btnIniciarSesion;
    private JButton btnSalir;

    public LoginView() {
        setTitle("Biblioteca Escolar - Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        crearComponentes();
    }

    private void crearComponentes() {
        setLayout(null);

        JLabel lblTitulo = new JLabel("Inicio de Sesión");
        lblTitulo.setBounds(130, 10, 180, 25);
        add(lblTitulo);

        JLabel lblCorreo = new JLabel("Correo:");
        lblCorreo.setBounds(30, 45, 100, 30);
        add(lblCorreo);

        txtCorreo = new JTextField();
        txtCorreo.setBounds(130, 45, 200, 30);
        add(txtCorreo);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(30, 85, 100, 30);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(130, 85, 200, 30);
        add(txtPassword);

        btnIniciarSesion = new JButton("Iniciar Sesión");
        btnIniciarSesion.setBounds(30, 135, 160, 30);
        add(btnIniciarSesion);

        btnSalir = new JButton("Salir");
        btnSalir.setBounds(210, 135, 120, 30);
        add(btnSalir);

        btnIniciarSesion.addActionListener(e -> iniciarSesion());
        btnSalir.addActionListener(e -> System.exit(0));
    }

    private void iniciarSesion() {
        String correo = txtCorreo.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (correo.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor, complete todos los campos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            Usuario usuario =
                    usuarioDAO.validarLogin(correo, password);

            if (usuario != null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Inicio de sesión exitoso. Bienvenido/a "
                                + usuario.getNombre()
                );

                MenuPrincipalView menu =
                        new MenuPrincipalView(usuario);

                menu.setVisible(true);
                dispose();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Correo o contraseña incorrectos",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al iniciar sesión. Verifica la conexión "
                            + "con la base de datos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
}