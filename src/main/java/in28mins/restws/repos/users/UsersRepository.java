package in28mins.restws.repos.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in28mins.restws.model.users.User;

@Repository
public interface UsersRepository extends JpaRepository<User, Integer>
{

}
