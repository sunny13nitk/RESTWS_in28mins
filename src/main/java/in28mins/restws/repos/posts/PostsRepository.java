package in28mins.restws.repos.posts;

import org.springframework.data.jpa.repository.JpaRepository;

import in28mins.restws.model.posts.Post;

public interface PostsRepository extends JpaRepository<Post, Integer>
{

}
