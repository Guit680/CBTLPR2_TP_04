import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TP04 extends JFrame implements ActionListener {

    private JLabel     lblBusca        = new JLabel("Nome:");
    private JTextField txtBusca        = new JTextField(20);
    private JButton    btnPesquisar    = new JButton("Pesquisar");

    private JLabel lblNomeTitulo    = new JLabel("Nome:");
    private JLabel lblSalarioTitulo = new JLabel("Salário:");
    private JLabel lblCargoTitulo   = new JLabel("Cargo:");

    private JLabel lblNomeVal    = new JLabel("");
    private JLabel lblSalarioVal = new JLabel("");
    private JLabel lblCargoVal   = new JLabel("");

    private JButton btnAnterior = new JButton("Anterior");
    private JButton btnProximo  = new JButton("Próximo");

    private List<String[]> resultados = new ArrayList<>();
    private int indiceAtual = -1;

    private Connection con;

    public TP04() {
        super("TRABALHO PRATICO 04");
        setSize(400, 280);
        setLocation(100, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        JPanel painelBusca = new JPanel(new GridLayout(2, 1));

        JPanel linhaNome = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        linhaNome.add(lblBusca);
        linhaNome.add(txtBusca);

        JPanel linhaPesquisar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linhaPesquisar.add(btnPesquisar);

        painelBusca.add(linhaNome);
        painelBusca.add(linhaPesquisar);

        JPanel painelResultados = new JPanel(new GridLayout(3, 2, 10, 5));
        painelResultados.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        Font negrito = new Font("Arial", Font.BOLD, 13);
        lblNomeTitulo.setFont(negrito);
        lblSalarioTitulo.setFont(negrito);
        lblCargoTitulo.setFont(negrito);

        painelResultados.add(lblNomeTitulo);
        painelResultados.add(lblNomeVal);
        painelResultados.add(lblSalarioTitulo);
        painelResultados.add(lblSalarioVal);
        painelResultados.add(lblCargoTitulo);
        painelResultados.add(lblCargoVal);

        JPanel painelNav = new JPanel(new GridLayout(1, 2));
        painelNav.add(btnAnterior);
        painelNav.add(btnProximo);

        btnPesquisar.addActionListener(this);
        btnAnterior.addActionListener(this);
        btnProximo.addActionListener(this);

        add(painelBusca,     BorderLayout.NORTH);
        add(painelResultados, BorderLayout.CENTER);
        add(painelNav,        BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try { if (con != null) con.close(); } catch (Exception ex) {}
            }
        });

        conectar();
        setVisible(true);
    }

    private void conectar() {
        try {
            String url = "jdbc:sqlserver://127.0.0.1\\SQLEXPRESS;"
                       + "databaseName=aulajava;"
                       + "integratedSecurity=true;"
                       + "trustServerCertificate=true;";
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection(url);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao conectar ao banco:\n" + e.getMessage(),
                "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnPesquisar) {
            pesquisar();
        } else if (e.getSource() == btnProximo) {
            proximo();
        } else if (e.getSource() == btnAnterior) {
            anterior();
        }
    }

    private void pesquisar() {
        resultados.clear();
        indiceAtual = -1;
        limparResultados();

        if (con == null) {
            JOptionPane.showMessageDialog(this,
                "Sem conexão com o banco de dados.",
                "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String sql = "SELECT f.nome_func, f.sal_func, c.ds_cargo "
                       + "FROM tbfuncs f "
                       + "JOIN tbcargos c ON f.cod_cargo = c.cd_cargo "
                       + "WHERE f.nome_func LIKE ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + txtBusca.getText().trim() + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] linha = {
                    rs.getString("nome_func").trim(),
                    String.format("%.2f", rs.getBigDecimal("sal_func")),
                    rs.getString("ds_cargo").trim()
                };
                resultados.add(linha);
            }

            rs.close();
            ps.close();

            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Nenhum registro encontrado.",
                    "Pesquisa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                indiceAtual = 0;
                exibirRegistro();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erro na pesquisa:\n" + ex.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void proximo() {
        if (resultados.isEmpty()) return;
        if (indiceAtual < resultados.size() - 1) {
            indiceAtual++;
            exibirRegistro();
        } else {
            JOptionPane.showMessageDialog(this,
                "Este é o último registro.",
                "Navegação", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void anterior() {
        if (resultados.isEmpty()) return;
        if (indiceAtual > 0) {
            indiceAtual--;
            exibirRegistro();
        } else {
            JOptionPane.showMessageDialog(this,
                "Este é o primeiro registro.",
                "Navegação", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exibirRegistro() {
        if (indiceAtual >= 0 && indiceAtual < resultados.size()) {
            String[] reg = resultados.get(indiceAtual);
            lblNomeVal.setText(reg[0]);
            lblSalarioVal.setText(reg[1]);
            lblCargoVal.setText(reg[2]);
        }
    }

    private void limparResultados() {
        lblNomeVal.setText("");
        lblSalarioVal.setText("");
        lblCargoVal.setText("");
    }

    public static void main(String[] args) {
        new TP04();
    }
}
