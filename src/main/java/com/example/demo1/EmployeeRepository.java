package com.example.demo1;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository
{
    public static Connection getConnection()
    {
        
        Connection connection = null;
        String url = "jdbc:postgresql://localhost:5432/employee";
        String user = "postgres";
        String password = "postgres";
        
        try
        {
            //Class.forName("org.postgresql.Driver"); For Tomcat Local Server
            connection = DriverManager.getConnection( url , user , password );
            if ( connection != null )
            {
                System.out.println( "Connected to the PostgreSQL server successfully." );
            }
            else
            {
                System.out.println( "Failed to make connection!" );
            }
        }
        catch ( SQLException e )
        {
            getSQLExceptionInfo( e );
        }
        
        return connection;
    }
    
    public static int save( Employee employee )
    {
        int status = 0;
        try
        {
            Connection connection = EmployeeRepository.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "insert into users(name,surname,birthdate,country,phoneNumber,email,isdeleted) values (?,?,?,?,?,?,?)" );
            ps.setString( 1 , employee.getName() );
            ps.setString( 2 , employee.getSurname() );
            ps.setString( 3 , employee.getBirthdate() );
            ps.setString( 4 , employee.getCountry() );
            ps.setString( 5 , employee.getPhoneNumber() );
            ps.setString( 6 , employee.getEmail() );
            ps.setBoolean( 7 , employee.getIsDeleted() );
            
            status = ps.executeUpdate();
            connection.close();
            
        }
        catch ( SQLException e )
        {
            getSQLExceptionInfo( e );
        }
        return status;
    }
    
    public static int update( Employee employee )
    {
        
        int status = 0;
        
        try
        {
            Connection connection = EmployeeRepository.getConnection();
            try ( PreparedStatement ps = connection.prepareStatement(
                    "update users set name=?,surname=?,birthdate=?,country=?,phoneNumber=?,email=? where id=?" ) )
            {
                ps.setString( 1 , employee.getName() );
                ps.setString( 2 , employee.getSurname() );
                ps.setString( 3 , employee.getBirthdate() );
                ps.setString( 4 , employee.getCountry() );
                ps.setString( 5 , employee.getPhoneNumber() );
                ps.setString( 6 , employee.getEmail() );
                ps.setInt( 7 , employee.getId() );
                
                status = ps.executeUpdate();
            }
            connection.close();
            
        }
        catch ( SQLException e )
        {
            getSQLExceptionInfo( e );
        }
        return status;
    }
    
    public static int delete( int id )
    {
        int status = 0;
        
        try
        {
            Employee employee = new Employee();
            Connection connection = EmployeeRepository.getConnection();
            PreparedStatement ps = connection.prepareStatement( "select * from users where id=?" );
            ps.setInt( 1 , id );
            ResultSet rs = ps.executeQuery();
            
            if ( rs.next() )
            {
                setEmployee( employee , rs );
                if ( employee.getIsDeleted() ) return 0;
            }
            
            ps = connection.prepareStatement(
                    "update users set isdeleted=true where id=?" );
            ps.setInt( 1 , id );
            
            status = ps.executeUpdate();
            
            connection.close();
        }
        catch ( SQLException e )
        {
            getSQLExceptionInfo( e );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return status;
    }
    
    public static Employee getEmployeeById( int id )
    {
        Employee employee = new Employee();
        try
        {
            Connection connection = EmployeeRepository.getConnection();
            PreparedStatement ps = connection.prepareStatement( "select * from users where id=?" );
            ps.setInt( 1 , id );
            ResultSet rs = ps.executeQuery();
            
            if ( rs.next() )
            {
                setEmployee( employee , rs );
                if ( employee.getIsDeleted() ) return null;
            }
            connection.close();
            
        }
        catch ( SQLException e )
        {
            getSQLExceptionInfo( e );
        }
        return employee;
    }
    
    public static List <Employee> getAllEmployees()
    {
        
        List <Employee> listEmployees = new ArrayList <>();
        
        try
        {
            Connection connection = EmployeeRepository.getConnection();
            PreparedStatement ps = connection.prepareStatement( "select * from users order by id" );
            ResultSet rs = ps.executeQuery();
            
            while ( rs.next() )
            {
                Employee employee = new Employee();
                setEmployee( employee , rs );
                if ( !employee.getIsDeleted() )
                { listEmployees.add( employee ); }
            }
            connection.close();
            
        }
        catch ( SQLException e )
        {
            getSQLExceptionInfo( e );
        }
        
        return listEmployees;
    }
    
    private static void getSQLExceptionInfo( SQLException e )
    {
        System.out.println( "SQLException message:" + e.getMessage() );
        System.out.println( "SQLException SQL state:" + e.getSQLState() );
        System.out.println( "SQLException SQL error code:" + e.getErrorCode() );
    }
    
    private static void setEmployee( Employee employee , ResultSet rs )
    throws SQLException
    {
        employee.setId( rs.getInt( 1 ) );
        employee.setName( rs.getString( 2 ) );
        employee.setSurname( rs.getString( 3 ) );
        employee.setBirthdate( rs.getString( 4 ) );
        employee.setCountry( rs.getString( 5 ) );
        employee.setPhoneNumber( rs.getString( 6 ) );
        employee.setEmail( rs.getString( 7 ) );
        employee.setIsDeleted( rs.getBoolean( 8 ) );
    }
    
    
    public static void setEmployeeByRequest( Employee employee , HttpServletRequest request )
    {
        employee.setName( request.getParameter( "name" ) );
        employee.setSurname( request.getParameter( "surname" ) );
        employee.setBirthdate( request.getParameter( "birthdate" ) );
        employee.setCountry( request.getParameter( "country" ) );
        employee.setPhoneNumber( request.getParameter( "phoneNumber" ) );
        employee.setEmail( request.getParameter( "email" ) );
        employee.setIsDeleted( Boolean.parseBoolean( request.getParameter( "isDeleted" ) ) );
    }
}
