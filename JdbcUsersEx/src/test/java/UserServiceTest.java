import com.krootix.dao.UserDaoJdbcImpl;
import com.krootix.entity.User;
import com.krootix.service.UserService;
import com.krootix.utils.DBSystemException;
import com.krootix.utils.tx.TransactionManager;
import com.krootix.utils.tx.TransactionManagerImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserDaoJdbcImpl userDaoJdbc;
    @Mock
    TransactionManager txManager = Mockito.mock(TransactionManagerImpl.class);
    @Mock
    User user;

    @Mock
    private Callable<User> callable;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
        //callable = Mockito.mock(Callable.class);
    }

    @Test
    public void findByIdVerifyTest() throws Exception {
        MockitoAnnotations.initMocks(true);
        UserService userService = new UserService(userDaoJdbc);
        userService.findUser(1);
        verify(userDaoJdbc).findById(1);
    }

    @Test
    public void findByIdTest() throws Exception {
        //MockitoAnnotations.initMocks(true);
        UserService userService = new UserService(userDaoJdbc);
        when(userDaoJdbc.findById(1)).thenReturn(createTestEntityUser());
        User actualUser = userService.findUser(1);
        Assert.assertEquals(1, actualUser.getId());
        Assert.assertEquals("Mike", actualUser.getName());
        Assert.assertEquals("Malone", actualUser.getSurname());
        Assert.assertEquals("Mike99@mail.ru", actualUser.getEmail());
        verify(userDaoJdbc).findById(1);
    }

    private User createTestEntityUser() {
        User user = new User();
        user.setId(1);
        user.setName("Mike");
        user.setSurname("Malone");
        user.setEmail("Mike99@mail.ru");
        return user;
    }

    @Test
    public void UpdateVerifyTest() throws Exception {
        MockitoAnnotations.initMocks(true);
        UserService userService = new UserService(userDaoJdbc);
        String surname = "Block";
        userService.updateSurname(1, surname);
        verify(userDaoJdbc).updateSurname(1, surname);
    }

    @Test
    public void updateUserSurnameTest() throws Exception {
        //MockitoAnnotations.initMocks(true);
        UserService userService = new UserService(userDaoJdbc);
        String surname = "Block";
        when(userDaoJdbc.updateSurname(1, surname)).thenReturn(1);
        userService.updateSurname(1, surname);
        Assert.assertEquals(1, userDaoJdbc.updateSurname(1, surname));
        //verify(userDaoJdbc).updateSurname(1, surname);
        verify(userDaoJdbc, times(2)).updateSurname(1, surname);
    }

    @Test
    public void UpdateUserLongSurnameThrowExeptionTest() throws Exception {
        UserService userService = new UserService(userDaoJdbc);
        expectedEx.expect(DBSystemException.class);
        //expectedEx.expectMessage("Can't execute SQL = 'UPDATE Users SET surname = ? WHERE id = ?'");
        StringBuilder surname = new StringBuilder();
        surname.setLength(51);
        when(userDaoJdbc.updateSurname(1, surname.toString())).thenThrow(DBSystemException.class);
        userService.updateSurname(1, surname.toString());
    }

    @Test
    public void UpdateUserNullSurnameThrowExeptionTest() throws Exception {
        UserService userService = new UserService(userDaoJdbc);
        expectedEx.expect(DBSystemException.class);
        when(userDaoJdbc.updateSurname(1, null)).thenThrow(DBSystemException.class);
        userService.updateSurname(1, null);
    }

    @Test
    public void selectAllVerifyTest() throws Exception {
        MockitoAnnotations.initMocks(true);
        UserService userService = new UserService(userDaoJdbc);
        userService.SelectAll();
        verify(userDaoJdbc).selectAll();
    }

    @Test
    public void selectAllTest() throws Exception {
        MockitoAnnotations.initMocks(true);
        List<User> users = createTestListOfUsers();
        UserService userService = new UserService(userDaoJdbc);
        userService.SelectAll();
        verify(userDaoJdbc).selectAll();

        when(userDaoJdbc.selectAll()).thenReturn(users);
        userService.SelectAll();
        Assert.assertEquals(users, userDaoJdbc.selectAll());
    }

    private List<User> createTestListOfUsers() {
        User user1 = new User();
        user1.setId(1);
        user1.setName("Mike");
        user1.setSurname("Malone");
        user1.setEmail("Mike99@mail.ru");
        User user2 = new User();
        user2.setId(2);
        user2.setName("Nick");
        user2.setSurname("Sanchos");
        user2.setEmail("NickS@mail.ru");
        User user3 = new User();
        user3.setId(3);
        user3.setName("Alice");
        user3.setSurname("Grey");
        user3.setEmail("Alice23@mail.ru");
        List<User> users = new ArrayList<User>() {
            {
                add(user1);
                add(user2);
                add(user3);
            }
        };
        return users;
    }

    @Test
    public void createDataBaseTest() throws Exception {
        MockitoAnnotations.initMocks(true);
        UserService userService = new UserService(userDaoJdbc);
        userService.createDataBase();
        verify(userDaoJdbc,times(1)).createDbUser();
    }
}
