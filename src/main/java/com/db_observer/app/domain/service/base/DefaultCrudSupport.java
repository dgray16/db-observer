package com.db_observer.app.domain.service.base;

import com.db_observer.app.common.PermissionChecker;
import com.db_observer.app.domain.model.entity.base.AbstractIdentifiable;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class DefaultCrudSupport<E extends AbstractIdentifiable> {

    JpaRepository<E, Long> repository;

    public Optional<E> findById(Long entityId) {
        return repository.findById(entityId);
    }

    public E getById(Long entityId) {
        return repository.findById(entityId).orElseThrow(() -> isNotFound(entityId));
    }

    public E getOne(Long entityId) {
        return repository.getOne(entityId);
    }

    public List<E> findAll() {
        return Lists.newArrayList(repository.findAll());
    }

    public Page<E> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<E> findAll(Collection<Long> ids) {
        return Lists.newArrayList(repository.findAllById(ids));
    }

    public List<E> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    public E update(E entity) {
        Objects.requireNonNull(entity.getId(), "Could not update entity. Entity hasn't persisted yet");
        return repository.save(entity);
    }

    public E create(E entity) {
        PermissionChecker.check(
                Objects.isNull(entity.getId()),
                "Cannot create entity",
                IllegalArgumentException::new
        );
        return repository.save(entity);
    }

    @SneakyThrows
    public E create(Consumer<E> consumer) {
        E objectInstance = (E) getClassRawType().getDeclaredConstructor().newInstance();
        consumer.accept(objectInstance);

        return create(objectInstance);
    }

    public void createAll(Collection<E> entities) {
        entities.forEach(entity -> PermissionChecker.check(
                Objects.isNull(entity.getId()), "Cannot create entity", IllegalArgumentException::new
        ));
        repository.saveAll(entities);
    }

    public void delete(E entity) {
        Objects.requireNonNull(entity.getId(), "Could not delete entity. Entity hasn't persisted yet");
        repository.delete(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private EmptyResultDataAccessException isNotFound(Long entityId) {
        String simpleName = getClassRawType().getSimpleName();
        return new EmptyResultDataAccessException(
                String.format("Entity (%s) was not found by ID: %s", simpleName, entityId), 1
        );
    }

    private Class<? super E> getClassRawType() {
        TypeToken<E> typeToken = new TypeToken<>(getClass()) {};
        return typeToken.getRawType();
    }

}
