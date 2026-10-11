package vistas;

import models.Usuario;
import models.Rol;

import javax.swing.*;

public class MenuPrincipalView extends JFrame {

    private Usuario usuario;

    private JButton btnLibros;
    private JButton btnEstudiantes;
    private JButton btnPrestamos;
    private JButton btnReportes;
    private JButton btnCerrarSesion;

    public MenuPrincipalView(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Biblioteca Escolar - Menú Principal");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        crearComponentes();
    }

    private void crearComponentes() {
        setLayout(null);

        JLabel lblTitulo = new JLabel(
                "Sistema de Gestión de Biblioteca"
        );
        lblTitulo.setBounds(75, 20, 320, 30);
        add(lblTitulo);

        JLabel lblBienvenida = new JLabel(
                "Bienvenido/a: " + usuario.getNombre()
        );
        lblBienvenida.setBounds(30, 60, 350, 25);
        add(lblBienvenida);

        JLabel lblRol = new JLabel(
                "Rol: " + usuario.getRol()
        );
        lblRol.setBounds(30, 85, 250, 25);
        add(lblRol);

        btnLibros = new JButton("Gestión de libros");
        btnLibros.setBounds(80, 125, 280, 35);
        add(btnLibros);

        btnEstudiantes = new JButton("Gestión de estudiantes");
        btnEstudiantes.setBounds(80, 170, 280, 35);
        add(btnEstudiantes);

        btnPrestamos = new JButton("Préstamos y devoluciones");
        btnPrestamos.setBounds(80, 215, 280, 35);
        add(btnPrestamos);

        btnReportes = new JButton("Reportes");
        btnReportes.setBounds(80, 260, 280, 35);
        add(btnReportes);

        btnCerrarSesion = new JButton("Cerrar sesión");
        btnCerrarSesion.setBounds(80, 310, 280, 30);
        add(btnCerrarSesion);

        configurarPermisos();


        btnLibros.addActionListener(e -> {
            GestionLibrosView librosView =
                    new GestionLibrosView();

            librosView.setVisible(true);
        });


        btnEstudiantes.addActionListener(e -> {
            GestionEstudiantesView estudiantesView =
                    new GestionEstudiantesView();

            estudiantesView.setVisible(true);
        });

        btnPrestamos.addActionListener(e -> {
            GestionPrestamosView prestamosView =
                    new GestionPrestamosView(usuario);

            prestamosView.setVisible(true);
        });


        btnReportes.addActionListener(e -> {
            GestionReportesView reportesView = new GestionReportesView();
            reportesView.setVisible(true);
        });


        btnCerrarSesion.addActionListener(e -> {
            LoginView login = new LoginView();
            login.setVisible(true);
            dispose();
        });
    }

    private void configurarPermisos() {

        boolean esBibliotecario =
                usuario.getRol() == Rol.BIBLIOTECARIO;

        btnLibros.setVisible(esBibliotecario);
        btnEstudiantes.setVisible(esBibliotecario);
        btnReportes.setVisible(esBibliotecario);

        btnPrestamos.setVisible(true);
    }
}