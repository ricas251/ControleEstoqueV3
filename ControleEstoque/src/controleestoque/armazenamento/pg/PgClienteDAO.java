/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controleestoque.armazenamento.pg;

import controleestoque.armazenamento.ClienteDAO;
import controleestoque.entidades.Cliente;
import controleestoque.entidades.ClientePessoaFisica;
import controleestoque.entidades.ClientePessoaJuridica;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author Carlos Roberto Ricas da Silva <carlosricas251@hotmail.com>3
 * 
 */
public class PgClienteDAO implements ClienteDAO{

    @Override
    public Cliente buscar(Cliente cliente) {
        try {
            PreparedStatement ps = 
                    PostgreSqlDAOFactory.getConnection().prepareStatement(
                    "select endereco,telefone,email,tipo "
                    + "from cliente where codigo = ?");
            ps.setLong(1, cliente.getCodigo());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String endereco = rs.getString(1);
                String telefone = rs.getString(2);
                String email = rs.getString(3);
                char tipo = rs.getString(4).charAt(0);
                
                if (tipo == 'F') {
                    PreparedStatement ps1= 
                    PostgreSqlDAOFactory.getConnection().prepareStatement(
                    "select cpf,sexo,nome,datanascimento "
                    + "from clientePessoaFisica where codigo = ?");
                    ps1.setLong(1, cliente.getCodigo());
                    ResultSet rs1 = ps1.executeQuery();
                    
                    if (rs1.next()){
                        Long cpf = Long.parseLong (rs1.getString(1));
                        char sexo = rs1.getString(2).charAt(0);
                        String nome = rs1.getString(3);
                        Date dataNascimento = rs1.getDate(4);

                        ClientePessoaFisica clientePF = new ClientePessoaFisica(
                                cliente.getCodigo(), endereco, telefone, email,
                                cpf, sexo, nome, dataNascimento);
                        return clientePF;
                    }
                } else if (tipo == 'J'){
                     PreparedStatement ps2= 
                    PostgreSqlDAOFactory.getConnection().prepareStatement(
                    "select cnpj,inscricaoEstadual,nomeFantasia,razaoSocial"
                    + "from clientePessoaJuridica where codigo = ?");
                    ps2.setLong(1, cliente.getCodigo());
                    ResultSet rs2 = ps2.executeQuery();
                    
                    if(rs2.next()){
                        long cnpj= Long.parseLong(rs2.getString(1));
                        long inscricaoEstadual= Long.parseLong(rs2.getString(2));
                        String nomeFantasia= rs2.getString(3);
                        String razaoSocial= rs2.getString(4);
                        ClientePessoaJuridica clientePJ = new ClientePessoaJuridica(
                                cliente.getCodigo(), endereco, telefone, email,
                                cnpj, inscricaoEstadual, nomeFantasia,
                                razaoSocial);
                        return clientePJ;
                    }
                }

            }
            
        } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, 
                    "Ocorreu um erro ao obter registro do cliente.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    @Override
    public boolean inserir(Cliente cliente) {
        try {
            PreparedStatement ps = PostgreSqlDAOFactory.getConnection().
                    prepareStatement("insert into cliente (endereco,"
                    + " telefone,email,tipo) values (?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, cliente.getEndereco());
            ps.setString(2, cliente.getTelefone());
            ps.setString(3, cliente.getEmail());
            char tipo = cliente instanceof ClientePessoaFisica ? 'F' : 'J';
            ps.setString(4, Character.toString(tipo));
            
            int resultado = ps.executeUpdate();
            
            if (resultado == 1){
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                long codigo = rs.getLong(1);
                
                if (cliente instanceof ClientePessoaFisica){
                    PreparedStatement ps1 = PostgreSqlDAOFactory.getConnection().prepareStatement("insert into clientePessoaFisica (codigo,cpf,sexo,nome,datanascimento) values (?,?,?,?,?)");
                    ps1.setLong(1, codigo);
                    ClientePessoaFisica clientePF = (ClientePessoaFisica) cliente;
                    ps1.setString(2, Long.toString(clientePF.getCpf()));
                    ps1.setString(3, Character.toString(clientePF.getSexo()));
                    ps1.setString(4, clientePF.getNome());
                    ps1.setDate(5, new java.sql.Date(clientePF.getDataNascimento().getTime()));
                    
                    resultado = ps1.executeUpdate();
                    return resultado == 1;
                } else if (cliente instanceof ClientePessoaJuridica){
                    
                }
            }
            
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(null, 
                    "Ocorreu um erro ao inserir o  registro do cliente.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    @Override
    public boolean alterar(Cliente cliente) {
    }

    @Override
    public boolean excluir(Cliente cliente) {
    }

    @Override
    public ArrayList<Cliente> getLista() {
    }
    
}
