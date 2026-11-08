package com.suhsaechan.dongbanza.member.repository;

import com.suhsaechan.dongbanza.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
}
